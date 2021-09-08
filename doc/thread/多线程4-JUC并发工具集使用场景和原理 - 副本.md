#阻塞队列

## SynchronousQueue

	1. 通过信息的传递来实现生产者和消费者的阻塞和唤醒

	2. 存储处于阻塞状态下的生产者线程、消费者线程

	
	1000请求丢入线程池 -》 必须找一个消费者线程来处理

## LinkedTransferQueue

	1. 无界阻塞队列

	2. transfer能力

	3. LinkedBlockingQueue + TransferQueue

## LinkedBlockingQueue

	1.一个正常的基于链表结构的阻塞队列，无界队列


## LinkedBlckingDeque
	
	1. 双向链表组成的队列

	2. 支持双向插入和移除

	3. 在一定程度上能够解决多线程的竞争问题

	4. fork/join ->工具窃取

# 阻塞队列的使用

	1. 责任链模式

	2. 构建一条执行链路

# 阻塞队列的案例演示
	
	详情见chapter4包下代码

# J.U.C并发工具

## CountDownLatch
	
	1. await

	2. countDown

### CountDownLatch的实际应用

	1. 在启动应用的时候，去对第三方的应用做健康检测

### CountDownLatch的实现原理
	
	1. 它可以让一个线程阻塞

	2. 可以让多个线程zus

	3. 共享锁的实现。可以允许多个线程同事抢占到锁，然后等到计数器归零的时候，同事唤醒
		
		state记录计数器

		countDown的时候，实际上就是state

## Semaphore
	
	信号等
	
	限流器，限制资源的访问

	本质：抢占一个令牌-》如果抢占到令牌就同行，否则，就阻塞
	
		- acquire()抢占一个令牌
		- release()释放一个令牌

	详情见代码semaphore包下代码

	为什么要用共享锁？
		因为同事可以释放多个令牌，那么意味着可以同时有多个线程抢占到锁

## CyclicBarrier
	

