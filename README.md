### **BUYSELL**  

## **Описание проекта**  
BUYSELL – это простой REST-сервис на **Spring Boot**, предоставляющий API для работы с товарами.  

Функционал:  
- Запуск локального REST API.  
- GET-запрос с Query Parameters для фильтрации.  
- GET-запрос с Path Parameters для поиска товара по ID.  
- Подключение **CheckStyle** для кодстайла.  

## **Задание**  
1. **Создать и запустить локально REST-сервис** на Java (Spring Boot + Maven/Gradle).  
2. **Добавить GET эндпоинт с Query Parameters** для фильтрации товаров.  
3. **Добавить GET эндпоинт с Path Parameters** для поиска товара по ID.  
4. **Настроить CheckStyle** и исправить ошибки.  
5. **Формат ответа – JSON.**  

## **Установка и запуск**  
### **1. Клонирование репозитория**  
```sh
git clone https://github.com/your-repo/buysell.git
cd buysell
```

### **2. Сборка и запуск приложения**  
С использованием **Maven**:  
```sh
mvn clean install
mvn spring-boot:run
```
С использованием **Gradle**:  
```sh
gradle build
gradle bootRun
```

## **Доступные эндпоинты**  
### **Получение списка товаров с фильтрацией (Query Parameters)**  
```http
GET /products?title=Yogurt&price=100&city=New York&author=Author A
```
Пример ответа:  
```json
[
  {
    "id": 1,
    "title": "Yogurt",
    "description": "Description 1",
    "price": 100,
    "city": "New York",
    "author": "Author A"
  }
]
```

### **Получение товара по ID (Path Parameters)**  
```http
GET /products/{id}
```
Пример ответа:  
```json
{
  "id": 1,
  "title": "Yogurt",
  "description": "Description 1",
  "price": 100,
  "city": "New York",
  "author": "Author A"
}
```

Ты прав, стоит добавить инструкцию и для **Gradle**, чтобы покрыть оба варианта.  

### **Настройка CheckStyle**  

#### **Maven**  
Добавьте в `pom.xml`:  
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
    </configuration>
</plugin>
```
Запустите проверку:  
```sh
mvn checkstyle:check
```

#### **Gradle**  
Добавьте в `build.gradle`:  
```groovy
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.12.0'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}

tasks.withType(Checkstyle).configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true) 
    }
}
```
Запустите проверку:  
```sh
gradle check
```

## **Требования**  
- Java 17+  
- Spring Boot 3+  
- Maven/Gradle  

## **Авторы**  
Homak623
