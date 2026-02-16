# 📖 Проект: Confectionery Retail API 

Проект на базе **Spring Framework** на современной платформе **Java 21**.
---
![Java](https://img.shields.io/badge/java-21-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-%236DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-000000?style=for-the-badge&logo=json&logoColor=white)


## 🛠 Технологический стек
* **Java 21** (LTS) — использование современных возможностей языка.
* **Spring Boot 3.x** — актуальная версия фреймворка.
* **Maven** — управление зависимостями и сборка проекта.
* **Jackson** — библиотека для сериализации объектов в JSON.

---

## 🏗 Архитектурные принципы

### 1. Inversion of Control (IoC) & ApplicationContext
Управление жизненным циклом компонентов передано **IoC-контейнеру** Spring. Контейнер (**ApplicationContext**) автоматически обнаруживает классы, помеченные стереотипными аннотациями (`@RestController`, `@Service`), и регистрирует их как **Spring Beans**.

### 2. Dependency Injection (DI)
Реализовано **внедрение через конструктор** (Constructor Injection). 
* Это обеспечивает **immutability** (неизменяемость) зависимостей через использование `final` полей.
* Позволяет избегать скрытых зависимостей и упрощает Unit-тестирование без использования Reflection API.

### 3. Data Transfer Objects (DTO) via Java Records
Для передачи данных используются **Java Records** (введены как стандарт в Java 16, активно используются в Java 21). 
* Это компактные и неизменяемые классы, которые автоматически генерируют конструктор, методы `equals()`, `hashCode()` и `toString()`.
* Использование Records идеально подходит для слоя DTO, так как они семантически представляют собой «прозрачные носители данных».

---

## 📂 Структура проекта

* **Controller Layer** (`@RestController`): обрабатывает HTTP-запросы, управляет маршрутизацией и возвращает ответы клиенту.
* **Service Layer** (`@Service`): Слой бизнес-логики. Координирует выполнение операций, обращается к репозиторию и использует мапперы для подготовки данных.
* **Mapper Layer** (`@Component`): Отвечает за преобразование (маппинг) внутренних сущностей (Entity) в объекты передачи данных (DTO) и обратно.
* **Repository Layer** (`@Repository`): Слой доступа к данным. Инкапсулирует логику хранения и поиска объектов (In-Memory хранилище).
* **DTO Layer** (`record`): Data Transfer Objects. Компактные и неизменяемые модели данных для формирования чистых JSON-ответов.
---

## 🚀 API Endpoints

| Метод | Эндпоинт | Назначение | Техническая деталь |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Список всех изделий | — |
| `GET` | `/api/products/{id}` | Поиск по ID | Использование `@PathVariable` |
| `GET` | `/api/products/type/{type}` | Фильтр по категории | Использование `@PathVariable` |
| `GET` | `/api/products/description` | Описание по имени | Использование `@RequestParam` |



---

## ⚙️ Механизм обработки запроса
При поступлении запроса **DispatcherServlet** (Front Controller) делегирует задачу контроллеру. Результат выполнения (Record) передается в **HttpMessageConverter**, где библиотека **Jackson** выполняет сериализацию Java-объекта в JSON-строку для передачи в теле ответа (`@ResponseBody`).

---

## 📝 Запуск проекта
1. Убедитесь, что установлена **JDK 21**.
2. Сборка проекта: `mvn clean install`
3. Запуск приложения: `mvn spring-boot:run`
