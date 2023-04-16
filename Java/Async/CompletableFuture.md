# 16. CompletableFuture: 안정적 비동기 프로그래밍

## 16.1 비동기 API 구현
### getPrice 동기 메서드
```java

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        Util.delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

```
### 동기 메서드를 비동기 메서드로 변환
```java

    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();  // 계산 결과를 포함할 CompletableFuture를 생성한다
        new Thread(() -> {
            double price = calculatePrice(product); // 다른 스레드에서 비동기적으로 계산을 수행한다.
            futurePrice.complete(price);            // 오랜 시간이 걸리는 계산이 완료되면 Future에 값을 설정한다.
        }).start();
        return futurePrice;     // 계산 결과가 완료되길 기다리지 않고 Future를 반환한다.
    }

```

### CompletableFuture 내부에서 발생한 에러 전파
```java
    public Future<Double> getPriceAsyncV1(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            } catch (Exception ex) {
                futurePrice.completeExceptionally(ex);  // 도중에 문제가 발생하면 발생한 에러를 포함시켜 Future를 종료한다.
            }
        }).start();
        return futurePrice;
    }

```

### 팩토리 메서드 supplyAsync로 CompletableFuture 만들기
```java
    public Future<Double> getPriceAsyncV2(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }
```

## 16.2 비블록 코드 만들기

### 16.2.1 모든 상점에 순차적으로 정보를 요청하는 findPrices
```java
public class BestPriceFinder {

    private final List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"));

    public List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }
}
```

#### 실행결과
```
[BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74]
Done in 4035 msecs
```

### 16.2.2 병렬 스트림으로 요청 병렬화
```java
    public List<String> findPricesV2(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }
```

#### 실행결과
```
[BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74]
Done in 1020 msecs
```

### 16.2.3 CompletableFuture로 비동기 호출 구현하기
```java
    public List<String> findPriceV3(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + " price is " + shop.getPrice(product)))
                .toList();

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
```
#### 실행결과
```
[BestPrice price is 123.25651664705744, LetsSaveBig price is 169.4653393606115, MyFavoriteShop price is 214.12914480588853, BuyItAll price is 184.74384995303313]
Done in 1036 msecs
```
병렬 스트림을 사용한 구현보다는 느리다. 하지만 CompletableFuture는 병렬 스트림 버전에 비해 작업에 이용할 수 있는 다양한 Executor를 지정할 수 있다는 장점이 있다. 따라서 Executor로 스레드 풀의 크기를 조절하는 등 애플리케이션에 맞는 최적화된 설정을 만들 수 있다.

## 16.3 커스텀 Executor 사용하기
### 16.3.1 커스텀 Executor
```java
    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), // 상점 수만큼의 스레드를 갖는 풀을 생성한다(스레드 수의 범위는 0과 100사이)
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            });

```

```java

    public List<String> findPricesV4(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + " price is " + shop.getPrice(product), executor))    // custom executor사용 
                .toList();

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
```
#### 실행결과
```
[BestPrice price is 123.25651664705744, LetsSaveBig price is 169.4653393606115, MyFavoriteShop price is 214.12914480588853, BuyItAll price is 184.74384995303313]
Done in 1036 msecs
```
애플리케이션 특성에 맞는 Executor를 만들어 CompletableFuture를 활용하는 것이 바람직하다.


## 16.4 스트림 병렬화와 CompletableFuture 병렬화
CompletableFuture를 이용하면 전체적인 계산이 블록되지 않도록 스레드풀의 크기를 조절할 수 있고 다음을 참고하면 어떤 병렬화 기법을 사용할 것인지 선택하는 데 도움이 된다.

- I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다(모든 스레드가 계산 작업을 수행하는 상황에서는 프로세서 코어 수 이상의 스레드를 가질 필요가 없다).
- 반면 작업이 I/O를 기다리는 작업을 병렬로 실행할 때는 CompletableFuture가 더 많은 유연성을 제공하며 대기/계산(W/C)의 비율에 적합한 스레드 수를 설정할 수 있다. 특히 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측하기 어려운 문제도 있다.

## 16.4 비동기 작업 파이프라인 만들기

#### 서로 다른 할인률을 제공하는 다섯 가지 코드
```java
public class Discount {

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        Util.delay();       // Discount 서비스의 응답 지연을 흉내 낸다.
        return Util.format(price * (100 - code.percentage) / 100);
    }

}
```

#### 상점에서 제공한 문자열을 파싱하는 Quote클래스
```java
public class Quote {

    private final String shopName;
    private final double price;
    private final Code discountCode;

    public Quote(String shopName, double price, Code discountCode) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = discountCode;
    }

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Code discountCode = Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Code getDiscountCode() {
        return discountCode;
    }
}
```
#### Shop의 getPrice메서드 형식 및 로직 수정
```java

    public String getPriceV3(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[
                random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

```

### 16.4.1 할인 서비스 사용
```java
    
    public List<String> findPricesV5(String product) {
        return shops.stream()
                .map(shop -> shop.getPriceV3(product))    // 각 상점에서 할인 전 가격 얻기.
                .map(Quote::parse)      // 상점에서 반환한 문자열을 Quote 객체로 변환한다.
                .map(Discount::applyDiscount)       // Discount 서비스를 이용해서 각 Quote에 할인을 적용한다.
                .collect(Collectors.toList());
    }

```

#### 실행결과
```
[BestPrice price is 110.93, LetsSaveBig price is 135.58, MyFavoriteShop price is 192.72, BuyItAll price is 184.74]
Done in 8080 msecs
```

### 16.4.2 동기 작업과 비동기 작업 조합하기
```java

    public List<String> findPricesV6(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPriceV3(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)))
                .toList();

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

```
#### 실행결과
```
[BestPrice price is 110.93, LetsSaveBig price is 135.58, MyFavoriteShop price is 192.72, BuyItAll price is 184.74]
Done in 2051 msecs
```