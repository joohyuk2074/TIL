### try-finally구문을 사용하는 경우
#### 자원이 하나일 경우 try-finally를 사용하는 코드
```java
public class TopLine {
    // 코드 9-1 try-finally - 더 이상 자원을 회수하는 최선의 방책이 아니다! (47쪽)
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
}
```
위 코드가 딱히 문제가 되는건 아닙니다. BufferedReader에서 해당 path에 있는 파일을 읽고 finally에서 close로 자원을 닫는 것 까지 문제 없습니다.

#### 자원이 둘 이상일 경우 중첩 try-finally를 사용하는 경우
```java
public class Copy {
    private static final int BUFFER_SIZE = 8 * 1024;

    // 코드 9-2 자원이 둘 이상이면 try-finally 방식은 너무 지저분하다! (47쪽)
    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
```
하지만 자원이 2가지 이상일 경우 중첩으로 try-finally구문을 사용해야하기 때문에 가독성이 많이 떨어지게 됩니다. 그렇다면 InputStream, OutputStream을 둘다 하나의 try-finally에 생성하고 finally에 한번에 close하면 가독성이 좋아지지 않냐라고 생각하실 수도 있습니다.

#### 자원이 둘 이상일 경우 한번에 try-finally를 사용하는 경우
```java
public class Copy {
    private static final int BUFFER_SIZE = 8 * 1024;

    // 코드 9-2 자원이 둘 이상이면 try-finally 방식은 너무 지저분하다! (47쪽)
    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        } finally {
            in.close();
            out.close();
        }
    }
}
```
이렇게 하나의 try-finally 구문을 하나로 해서 여러개의 자원을 열고 닫도록 하면 안됩니다. 그 이유는 try블록 내에서 작업을 마치고 finally에서 in.close()를 하다가 예외가 발생하면 out.close()를 호출하지 않고 스레드를 종료해버리기 때문에 memory leak이 발생할 수 있습니다.

### try-finally-resources를 사용하는 경우
#### 자원이 하나일 경우 try-finally-resources를 사용하는 코드
```java
public class TopLine {
    // 코드 9-3 try-with-resources - 자원을 회수하는 최선책! (48쪽)
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }
}
```

#### 자원이 둘 이상일 경우 중첩 try-finally-resources를 사용하는 경우
```java
public class Copy {
    private static final int BUFFER_SIZE = 8 * 1024;

    // 코드 9-4 복수의 자원을 처리하는 try-with-resources - 짧고 매혹적이다! (49쪽)
    static void copy(String src, String dst) throws IOException {
        try (InputStream   in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        }
    }
}
```

try문 옆에 소괄호에서 자원을 생성하면 따로 명시적으로 finally문에서 close를 할 필요 없어 코드 가독성이 높아지고 특히 여러 자원일 경우에 이 효과가 더 커집니다.

```java
public interface Closeable extends AutoCloseable {

    public void close() throws IOException;
}
```

```java
public interface AutoCloseable {

    void close() throws IOException;
}
```
위의 Autocloseable, Closeable를 구현하고 있는 구현체 클래스들은 모두 사용 가능합니다. 해당 자원을 다 사용후에 close메서드를 호출해서 자원을 닫아주는데 BufferedReader는 Closeable인터페이스를 상속받기때문에 try-finally-resource구문에 자원을 선언할 수 있습니다. 지금까지는 try-fianlly-resources를 사용하면 가독성이 좋아지는 코드상으로만의 장점을 이야기 했는데 이 구문을 쓰면서 실용적인 장점이 있는데 예외를 잡아 먹지 않는다는 장점입니다. 이 이야기를 코드로 좀 더 풀어보겠습니다.


#### 고의적으로 readLine과 close메서드 호출 시 예외를 발생하도록하는 BadBufferedReader
```java
public class BadBufferedReader extends BufferedReader {
    public BadBufferedReader(Reader in, int sz) {
        super(in, sz);
    }

    public BadBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        throw new CharConversionException();
    }

    @Override
    public void close() throws IOException {
        throw new StreamCorruptedException();
    }
}
```

#### try-finally구문으로 BadBufferedReader를 사용하는 코드
```java
public class TopLine {
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BadBufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
        	br.close();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(firstLineOfFile("pom.xml"));
    }
}
```
try-finally구문으로 readLine메서드를 실행하면 CharConversionException이 발생합니다. 그 다음 finally블록에서 close메서드를 실행하면서 StreamCorruptedException이 발생합니다. 이런경우 코드를 실행해보면 예외가 어떻게 발생하는지 확인해 보겠습니다.

##### 실행결과
```
Exception in thread "main" java.io.StreamCorruptedException
	at me.whiteship.chapter01.item09.suppress.BadBufferedReader.close(BadBufferedReader.java:21)
	at me.whiteship.chapter01.item09.suppress.TopLine.firstLineOfFile(TopLine.java:19)
	at me.whiteship.chapter01.item09.suppress.TopLine.main(TopLine.java:24)
```
실행 결과 가장 나중에 발생한 예외인 StreamCorruptedException만 보이게 되고 br.readLine()에서 발생한 예외가 먹히기 때문에 디버깅을 할때 처음발생한 예외가 중요한데 이를 파악하기가 어려워 상당히 애를 먹게 됩니다.

#### try-finally-resources구문으로 BadBufferedReader를 사용하는 코드

```java
public class TopLine {
    static String firstLineOfFile(String path) throws IOException {
        try(BufferedReader br = new BadBufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(firstLineOfFile("pom.xml"));
    }
}
```
이번엔 try-finally-resource구문을 사용해서 실행해 보겠습니다

##### 실행결과
```java
Exception in thread "main" java.io.CharConversionException
	at me.whiteship.chapter01.item09.suppress.BadBufferedReader.readLine(BadBufferedReader.java:16)
	at me.whiteship.chapter01.item09.suppress.TopLine.firstLineOfFile(TopLine.java:11)
	at me.whiteship.chapter01.item09.suppress.TopLine.main(TopLine.java:16)
	Suppressed: java.io.StreamCorruptedException
		at me.whiteship.chapter01.item09.suppress.BadBufferedReader.close(BadBufferedReader.java:21)
		at me.whiteship.chapter01.item09.suppress.TopLine.firstLineOfFile(TopLine.java:10)
		... 1 more
```
실행결과 제일 최상위에 제일 먼저 발생한 CharConversionException가 먼저 보이고 그 아래에 그 다음 발생한 StreamCorruptedException까지 보입니다. 예외를 먹지 않고 최초에 어디서부터 예외가 발생했는지 파악이 가능해집니다. try-finally구문으로도 위의 실행결과처럼 나오게 할 수 있지만 코드가 상당히 지저분해지기때문에 try-finally-resources구문을 쓰면 가독성과 예외를 먹지않는 두가지 장점을 취할 수 있기때문에 자원을 이용할 때 try-finally-resources구문을 쓰시는걸 책에서도 강력히 추천하고 있습니다.

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#