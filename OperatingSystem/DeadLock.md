# Deadlock(교착상태)
두 개 이상의 프로세스 혹은 스레드가 서로가 가진 리소스를 기다리는 상태
## 1. 데드락을 만드는 네 가지 조건
- ### Mutual exclusion
    - 리소스(resource)를 공유해서 사용할 수 없다.
    - 리소스(resource)는 critical section, lock일 수도 있고 컴퓨터를 구성하는 cpu, momory, ssd, 모니터, 프린터 이런 모든 것들을 리소스(resource)라고 할 수 있다.

- ### Hold and Wait
    - 프로세스가 이미 하나 이상의 리소스를 취득한(hold) 상태에서 다른 프로세스가 사용하고 있는 리소스를 추가로 기다린다(wait)

- ### No preemption
    - 리소스 반환(release)은 오직 그 리소스를 취득한 프로세스만 할 수 있다.

- ### Circular wait
    - 프로세스들이 수환(circular) 형태로 서로의 리소스를 기다린다.

<hr>

## 2. OS의 데드락 해결 방법
### 2.1 데드락 방지(Deadlock prevention)
네 가지 조건 중 하나가 충족되지 않게 시스템을 디자인


#### 2.1.1 mutual exclusion 방지
리소스를 공유 가능하게 하는 방식인데 현실적으로 불가능하다. 공유자원의 동시진입을 방지하기 전에 critical section에 lock을 걸었고 프린트 같은 경우도 2개의 프로세스가 동시에 사용할 수 없다. 이런식으로 리소스의 특성상 mutual exclusion이 보장되어야 하는 리소스들이 있기 때문에 현실적으로는 불가능한 방법이다.
#### 2.1.2 Hold and Wait 방지
사용할 리소스들을 모두 획득한 뒤에 시작하고 리소스를 전혀 가지지 않은 상태에서만 리소스를 요청한다. 하지만 이방법도 특정 프로세스가 계속해서 리소스를 획득할 수 없는 starvation때문에 현실적으로 불가능하다.
#### 2.1.3 no preemption 방지
추가적인 리소스를 기다려야 한다면 이미 획득한 리소스를 다른 프로세스가 선점 가능하도록 한다.
#### 2.1.4 circular wait 방지
모든 리소스에 순서 체계를 부여해서 오름차순으로 리소스를 요청하는 방법인데 이 방법이 이전 방법들보다는 가장 많이 이용하는 방법이다.

<br>

### 2.2 데드락 회피(Deadlock avoidance)
실행 환경에서 추가적인 정보를 활용해서 데드락이 발생할 것 같은 상황을 회피하는 것
#### 2.2.1 Banker algorithm
리소스 요청을 허락해줬을 때 데드락이 발생할 가능성이 있으면 리소스를 할당해도 안전할 때 까지 계속 요청을 거절하는 알고리즘

<br>

### 2.3 데드락의 감지와 복구 전략
데드락을 허용하고 데드락이 발생하면 복구하는 전략.
#### 2.3.1 프로세스 종료
프로세스를 강제로 종료시키기 때문에 지금까지의 작업을 잃을 수도 있기 때문에 리스크가 큰 방식이다. 따라서 이런식의 접근은 최후의 방법으로 사용된다.
#### 2.3.2 리소스의 일시적인 선점을 허용
#### 2.3.3 데드락 무시
<hr>

## 3. Java에서 데드락

### 3.1 데드락이 발생할 수 있는 코드
```java
public class Main {
    public static void main(String[] args) {
        Object lock1 = new Object();
        Object lock2 = new Object();

        Thread t1 = new Thread(() -> { ... });

        Thread t2 = new Thread(() -> { ... }); 

        t1.start();
        t2.start();
    }

    Thread t1 = new Thread(() -> {
        synchronized (lock1) {
            System.out.println("[t1] get lock1");
            sunchronized (lock2) {
                System.out.println("[t1] get lock2");
            }
        }
    });

    Thread t2 = new Thread(() -> {
        synchronized (lock2) {
            System.out.println("[t2] get lock2");
            sunchronized (lock1) {
                System.out.println("[t2] get lock1");
            }
        }
    });
}
```

### 3.2 데드락을 방지 코드
```java
public class Main {
    Thread t1 = new Thread(() -> {
        synchronized (lock1) {
            System.out.println("[t1] get lock1");
            sunchronized (lock2) {
                System.out.println("[t1] get lock2");
            }
        }
    });

    Thread t2 = new Thread(() -> {
        synchronized (lock1) {
            System.out.println("[t2] get lock1");
            sunchronized (lock2) {
                System.out.println("[t2] get lock2");
            }
        }
    });
}
```


### 참고자료
- [쉬운코드](https://www.youtube.com/watch?v=ESXCSNGFVto&list=PLcXyemr8ZeoQOtSUjwaer0VMJSMfa-9G-&index=7)
