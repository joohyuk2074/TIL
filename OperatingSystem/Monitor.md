# 모니터(monitor)
- mutual exclusion을 보장
- 조건에 따라 스레드가 대기(waiting) 상태로 전환 가능
## 1. 모니터는 언제 사용되나?
- 한번에 하나의 스레드만 실행되어야 할 때
- 여러 스레드와 협업(cooperation)이 필요할 때
## 2. 모니터의 구성요소
- #### mutex 
    - critical section에 진입하려면 mutex lock을 취득해야 함
    - mutex lock을 취득하지 못한 스레드는 큐에 들어간 후 대기(waiting) 상태로 전환
    - mutex lock을 쥔 스레드가 lock을 반환하면 락을 기다리며 큐에 대기 상태로 있던 스레드 중 하나가 실행
- #### condition variable(s)
    - waiting queue를 가짐
    - 조건이 충족되길 기다리는 스레드들이 대기 상태로 머무는 곳

### 2.1 condition variable에서 주요 동작(operation)
- #### wait
    - thread가 자기 자신을 condition variable의 waiting queue에 넣고 대기 상태로 전환
- #### signal
    - waiting queue에서 대기중인 스레드 중 하나를 깨움
- #### broadcast
    - waiting queue에서 대기중인 스레드 전부를 깨움
- #### 두 개의 큐(queue)
    - entry queue: critical section에 진입을 기다리는 큐
    - waiting queue: 조건이 충족되길 기다리는 큐

<hr>


## 3. 모니터 Java코드 구현

```
acquire(m);                             // 모니터의 락 취득

while (!p) {                            // 조건 확인
    wait(m, cv);                        // 조건 충족 안되면 waiting
}

..... code

signal(cv2); -- OR -- broadcast(cv2)    // cv2가 cv와 같을 수도 있음

release(m);                             // 모니터의 락 변환
```

### 3.1 글로벌 변수

```java
global volatile Buffer q;
global Lock lock;
global CV fullCV;
global CV emptyCV;
```

### 3.2 Producer 스레드

```java
public method producer() {
    while(true) {
        task myTask = ...;

        lock.acquire();

        while (q.isFull()) {
            wait(lock, fullCV);
        }

        q.enqueue(myTask);

        signal(emptyCV) -- or-- broadcast(emptyCV);

        lock.release();
    }
}
```

### 3.3 Consumer 스레드
```java
public method consumer() {
    while(true) {
        lock.acquire();

        while(q.isEmpty()) {
            wait(lock, emptyCV);
        }

        myTask = q.dequeue();

        signal(fullCV); -- or -- broadcast(fullCV);

        lock.release();

        doStuff(myTask);
    }
}
```

<hr>

## 4. 자바에서 모니터란
- 자바에서 모든 객체는 내부적으로 모니터를 가지는데 모니터의 mutual exclusion 기능은 synchronized 키워드로 사용한다.
- 자바의 모니터는 condition variable를 하나만 가진다.
### 4.1 자바 모니터의 세 가지 동작
- wait
- notify
- notifyAll
### 4.2 Java 코드 예시
```java
class BoundedBuffer {
    private final init[] buffer = new int[5];
    private int count = 0;

    public synchronized void produc(int item) {
        while (count == 5) {
            wait(0;)
        }
        buffer[count++] = item;
        notifyAll();
    }

    public void consume() {
        int item = 0;
        synchronized(this) {
            while(count == 0) {
                wait();
            }
            item = buffer[--count];
            notifyAll();
        }
        System.out.println("Consume: " + item);
    }
}
```