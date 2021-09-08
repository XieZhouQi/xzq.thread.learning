# ConcurrentHashMap

##学习目标
	
	1. ConcurrentHashMap的存储结构
	2. ConcurrentHashMap一些重要的设计思想
		1. 并发扩容
		2. 高低位迁移
		3. 分段锁
		4. 红黑树
		5. 链表
##为什么要用ConcurrentHashMap？
	HashMap ->线程不安全的
	hashTable ->方法级别的锁（synchronized）性能问题

## ConcurrentHashMap使用
	
	java-8 引入lambda（匿名内部类的简化 也是一种语法糖）
	1. computeIfAbsent	如果key不存在，则调用后面的mappingFunction计算，把计算的返回值作	为value
	2. computeIfPresent 如果key存在，则修改，如果不存在，则返回null
	3. compute(computeIfAbsent与computeIfPresent组合)
	4. merge（合并数据）

## ConcurrentHashMap的存储结构和实现
	使用链表的形式存储是因为存在hash冲突

	JDK1.8
		1. 去掉了segment
		2. 引入了红黑树（解决链表过长，时间复杂度增加的问题）
		3. 什么时候转红黑树？
			数组长度（默认大小16）大于6 或者 链表长度大于8
		4. 红黑树在什么时候会转链表？
			扩容的时候。扩容后会导致链表的长度小于8,所以会执行红黑树转链表的逻辑
		5. 
	
	JDK1.7
		1. segment分段锁，锁的力度比较大


## 源码解析
	1. 源码注释理解查看包chm


	2. treeifyBin()方法
		1. 会根据阈值判断是转化为红黑树还是扩容
		2. 多线程并发扩容（允许多个线程协助扩容）
		3. 扩容的本质
			1. 创见一个新的数组（16-32，32-64）
			2. 然后把老的数据迁移到新的数组
			3. 多线程辅助扩容（针对老的数据，通过多个线程并行来执行数据的迁移过程）
				1. 记录当前的线程数量
				2. 当每个线程完成数据迁移之后，也就是退出的时候要减掉协助扩容的线程数量
		4. transfer(tab, nt)方法
			1. 实现数据转移
				需要计算当前线程的数据迁移空间

				创建一个新的数组（容量为扩容后的大小）

				如果是红黑树
					数据迁移后，不满足红黑树的条件，则红黑树转链表

				如果是链表

				高低位迁移
				

			2. 必须要有一个地方去记录，在当前扩容范围内，有多少个线程参与数据的迁移工作，必须要保证所有的线程完成了迁移的操作，才能表示扩容完成
				`
				static final int resizeStamp(int n) {
			        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
			    }`
				
				(1 << (RESIZE_STAMP_BITS - 1) //把计算出来的值的二进制的16位置为1

				stamp: 1000 0000 0001 1011 00000 00000 00000 00000

				(rs << RESIZE_STAMP_SHIFT) + 2 值变为：
				1000 0000 0001 1011 00000 00000 00000 00010 ->表示当前有一个线程来扩容


				高位16表示当前的扩容标记，保证唯一性
				低16位表示当前扩容的线程数量
				
	
#### 知识点

1. sizeCtl值变化及代表的含义
见图：[https://github.com/XieZhouQi/PhotoGallery/blob/main/chm-sizeCtl.png](https://github.com/XieZhouQi/PhotoGallery/blob/main/chm-sizeCtl.png "sizeCtl")

2. hash冲突怎么解决？、

3. 如何统计元素个数？
	1. 如果竞争不激烈的情况下，直接用cas(baseCount + 1)
	2. 如果竞争激烈的情况下，采用数组的方式来进行计数（countCell[]）
	3. 如图：[https://github.com/XieZhouQi/PhotoGallery/blob/main/chm-addCount.png](https://github.com/XieZhouQi/PhotoGallery/blob/main/chm-addCount.png "addCount")

## 红黑树
	1. 所有节点，在添加的时候，都是以红色节点来添加（比较少的可能性会破坏红黑树的结构）

### 红黑树平衡规则

	1. 红黑树的每个节点颜色只能是红色或者黑
	2. 根节点是黑色
	3. 如果当前的节点是红色，那么它的子节点必须是黑色
	4. 所有叶子节点（NIL节点，NIL节点表示叶子节点为空的节点）都是黑色	
	5. 从任一节点到其每个叶子节点的所有简单路径都包含相同数目的黑色节点

### 红黑树为了达到平衡
	1. 旋转
		1. 左旋
		2. 右旋
	2. 着色

# ConcurrentHashMap总结
	1. 使用java8
	2. 安全性的保障
	3. 原理分析
		1. put方法元素添加，构建数组，构建链表
		2. 解决hash冲突，——》链式寻址法
		3. 扩容——》数组的扩容
			1. 数据迁移
			2. 多线程并发协助数据迁移
			3. 高低位迁移，需要迁移的数据放在高位链，不需要迁移的数据放在低位链，然后一次性把高位和低位set到指定的数组下标
	4. 元素的统计
		1. 数组的方式，分片的设计思想
		2. 汇总数组+baseCount 的值来完成数据累加
	5. 当链表长度大于等于8，并且数组长度大于等于64的时候，链表转化为红黑树