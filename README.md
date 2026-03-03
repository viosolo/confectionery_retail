# 📖 Проект: Confectionery Retail API 
[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=viosolo_confectionery_retail)

В системе реализовано управление каталогом по трем основным категориям десертов:

🥯 Эклеры (Eclairs)

Описание: Классические французские пирожные из заварного теста.

Особенности: Начиняются нежным кремом и покрываются авторской глазурью.

☁️ Зефир (Zefir)

Описание: Легкое и воздушное лакомство.

Особенности: Изготавливается на основе натурального яблочного пектина с добавлением свежего ягодного и фруктового пюре.

🍪 Макаронс (Macarons)

Описание: Изящные десерты из миндальной муки.

Особенности: Хрустящие крышечки и разнообразные начинки — от классической соленой карамели до изысканной лаванды.
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

Архитектура приложения построена на четком разделении ответственности между слоями:

* **Controller Layer** (`@RestController`): Обрабатывает входящие HTTP-запросы, управляет маршрутизацией и отвечает за валидацию данных в `@RequestBody`.
* **Service Layer** (`@Service`): Центральный слой бизнес-логики. Координирует работу с БД, управляет транзакциями (`@Transactional`) и обеспечивает корректную связь между сущностями (Products, Categories, Ingredients).
* **Mapper Layer** (`@Component`): Инкапсулирует логику преобразования (mapping) между внутренними сущностями (**Entity**) и объектами передачи данных (**DTO**). Также отвечает за частичное обновление данных при `PATCH` запросах.
* **Repository Layer** (`@Repository`): Слой доступа к данным на базе **Spring Data JPA**. Использует возможности **PostgreSQL** для выполнения сложных запросов с оптимизацией (например, `JOIN FETCH` для решения проблемы N+1).
* **Entity Layer**: Объектно-реляционные модели (ORM) с настроенными связями:
    * `@ManyToOne` — связь продукта с категорией.
    * `@ManyToOne` — связь заказа с пользователем.
    * `@ManyToMany` — связь продуктов с ингредиентами через связующую таблицу.
    * `@ManyToMany` — связь продуктов с заказами через связующую таблицу.
---

## 🚀 API Endpoints

Реализованы для каждого класса entity Layer
---

## ⚙️ Механизм обработки запроса
При поступлении запроса **DispatcherServlet** (Front Controller) делегирует задачу контроллеру. Результат выполнения (Record) передается в **HttpMessageConverter**, где библиотека **Jackson** выполняет сериализацию Java-объекта в JSON-строку для передачи в теле ответа (`@ResponseBody`).

---

## 📝 Запуск проекта
1. Убедитесь, что установлена **JDK 21**.
2. Сборка проекта: `mvn clean install`
3. Запуск приложения: `mvn spring-boot:run`
