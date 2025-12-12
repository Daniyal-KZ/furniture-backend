# Furniture Backend API

## Обзор проекта

**Furniture Backend** - это REST API на Spring Boot, который анализирует описания мебели и рассчитывает необходимые материалы с помощью API OpenAI GPT. Приложение интегрируется с PostgreSQL для хранения данных и предоставляет endpoints для анализа мебели и управления материалами.

### Основные технологии
- **Фреймворк**: Spring Boot 3.5.7
- **Язык**: Java 21
- **База данных**: PostgreSQL
- **Сборщик**: Maven
- **ИИ интеграция**: OpenAI GPT-3.5 Turbo API
- **ORM**: Spring Data JPA / Hibernate

---

## Архитектура и компоненты

### Структура проекта

```
furniture-backend/
├── src/main/java/com/daniyal/furniturebackend/
│   ├── FurnitureBackendApplication.java      # Точка входа Spring Boot
│   ├── controller/                           # REST контроллеры
│   │   ├── FurnitureAnalysisController.java  # Endpoints анализа мебели
│   │   └── MaterialController.java           # Endpoints управления материалами
│   ├── model/                                # Модели данных
│   │   ├── Material.java                     # Сущность материала
│   │   ├── MaterialQuantity.java             # DTO количества материала
│   │   └── GptRequest.java                   # DTO запроса GPT
│   ├── repository/                           # Слой доступа к данным
│   │   └── MaterialRepository.java           # JPA репозиторий материалов
│   └── service/                              # Бизнес логика
│       └── GptService.java                   # Сервис интеграции GPT
└── src/main/resources/
    └── application.properties                 # Конфигурация приложения
```

---

## Документация классов

### 1. **FurnitureBackendApplication**
**Пакет**: `com.daniyal.furniturebackend`  
**Тип**: Spring Boot приложение

#### Описание
Главная точка входа приложения Spring Boot. Использует аннотацию `@SpringBootApplication` для авто-конфигурации и сканирования компонентов.

#### Основные возможности
- Инициализирует Spring контекст
- Запускает встроенный сервер на порту 8081
- Включает Spring Data JPA и Web MVC

#### Код
```java
@SpringBootApplication
public class FurnitureBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FurnitureBackendApplication.class, args);
    }
}
```

---

### 2. **FurnitureAnalysisController**
**Пакет**: `com.daniyal.furniturebackend.controller`  
**Тип**: REST контроллер

#### Описание
Обрабатывает HTTP запросы для анализа мебели. Предоставляет endpoints для анализа описаний мебели и извлечения необходимых материалов с помощью GPT.

#### Аннотации
- `@RestController`: Помечает класс как REST контроллер
- `@CrossOrigin(origins = "*")`: Включает CORS для всех источников
- `@RequestMapping("/api/furniture")`: Базовый путь для всех endpoints

#### Endpoints

##### POST `/api/furniture/analyze`
**Назначение**: Анализирует описание мебели и возвращает необходимые материалы с количествами.

**Тело запроса**:
```json
{
  "description": "деревянный обеденный стол с 4 стульями"
}
```

**Успешный ответ (200 OK)**:
```json
{
  "success": true,
  "description": "деревянный обеденный стол с 4 стульями",
  "materials": [
    {
      "name": "дерево",
      "quantity": 50.0
    },
    {
      "name": "гвозди",
      "quantity": 100.0
    }
  ]
}
```

**Ответ с ошибкой (400 Bad Request)**:
```json
{
  "success": false,
  "error": "Сообщение об ошибке"
}
```

**Зависимости**:
- Требует экземпляр `GptService` (внедряется через конструктор)
- Вызывает метод `GptService.analyzeFurniture()`

---

### 3. **MaterialController**
**Пакет**: `com.daniyal.furniturebackend.controller`  
**Тип**: REST контроллер

#### Описание
Управляет CRUD операциями для материалов мебели. Предоставляет endpoints для получения, добавления и массовой загрузки материалов в базу данных.

#### Аннотации
- `@RestController`: Помечает класс как REST контроллер
- `@CrossOrigin(origins = "*")`: Включает CORS для всех источников
- `@RequestMapping("/api/materials")`: Базовый путь для всех endpoints

#### Endpoints

##### GET `/api/materials`
**Назначение**: Получает все материалы из базы данных.

**Ответ (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "Дерево",
    "type": "Натуральный",
    "unit": "кг",
    "price": 15.50,
    "servicePrice": 20.00,
    "imageUrl": "https://example.com/wood.jpg"
  }
]
```

##### POST `/api/materials`
**Назначение**: Добавляет один материал в базу данных.

**Тело запроса**:
```json
{
  "name": "Кожа",
  "type": "Натуральный",
  "unit": "м²",
  "price": 45.00,
  "servicePrice": 55.00,
  "imageUrl": "https://example.com/leather.jpg"
}
```

**Ответ (200 OK)**: Возвращает созданный объект материала с присвоенным `id`.

##### POST `/api/materials/batch`
**Назначение**: Добавляет несколько материалов в базу данных за один запрос.

**Тело запроса**:
```json
[
  {
    "name": "Материал1",
    "type": "Тип1",
    "unit": "единица1",
    "price": 10.0,
    "servicePrice": 15.0,
    "imageUrl": "url1"
  }
]
```

**Ответ (200 OK)**: Возвращает список созданных объектов материалов.

---

### 4. **Material** (Сущность)
**Пакет**: `com.daniyal.furniturebackend.model`  
**Тип**: JPA сущность

#### Описание
Представляет материал мебели в базе данных. Использует аннотации Lombok для автоматической генерации геттеров/сеттеров.

#### Аннотации
- `@Entity`: Помечает класс как JPA сущность
- `@Setter/@Getter`: Аннотации Lombok для авто-генерации аксессоров

#### Поля

| Поле | Тип | Описание | Ограничения |
|------|-----|----------|-------------|
| `id` | Long | Первичный ключ | Авто-генерируемый (IDENTITY) |
| `name` | String | Название материала | например, "Дерево", "Сталь" |
| `type` | String | Тип/категория материала | например, "Натуральный", "Металл" |
| `unit` | String | Единица измерения | например, "кг", "м²", "литр" |
| `price` | double | Базовая цена за единицу | Числовое значение |
| `servicePrice` | Double | Цена услуги/труда за единицу | Может быть null, дополнительная стоимость |
| `imageUrl` | String | URL изображения материала | Для отображения в UI |

#### Сопоставление с БД
```
ТАБЛИЦА: material
├── id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
├── name (VARCHAR)
├── type (VARCHAR)
├── unit (VARCHAR)
├── price (DOUBLE)
├── service_price (DOUBLE, nullable)
└── image_url (VARCHAR)
```

---

### 5. **MaterialQuantity** (DTO)
**Пакет**: `com.daniyal.furniturebackend.model`  
**Тип**: Data Transfer Object (DTO)

#### Описание
Представляет материал с требуемым количеством для проекта мебели. Используется для ответов API анализа мебели.

#### Аннотации
- `@Setter/@Getter`: Аннотации Lombok для авто-генерации аксессоров

#### Поля

| Поле | Тип | Описание |
|------|-----|----------|
| `name` | String | Название материала |
| `quantity` | Double | Требуемое количество в единицах материала |

#### Конструкторы

**Конструктор 1: По умолчанию**
```java
public MaterialQuantity()
```
Используется для JSON десериализации.

**Конструктор 2: С параметрами**
```java
public MaterialQuantity(String name, Double quantity)
```
Используется при программном создании экземпляров.

#### Пример использования
```java
MaterialQuantity mq = new MaterialQuantity("Дерево", 50.0);
```

---

### 6. **GptRequest** (DTO)
**Пакет**: `com.daniyal.furniturebackend.model`  
**Тип**: Data Transfer Object (DTO)

#### Описание
Инкапсулирует payload запроса для анализа мебели. Содержит описание мебели для анализа GPT.

#### Аннотации
- `@Setter/@Getter`: Аннотации Lombok для авто-генерации аксессоров

#### Поля

| Поле | Тип | Описание |
|------|-----|----------|
| `description` | String | Описание мебели для анализа |

#### Конструкторы

**Конструктор 1: По умолчанию**
```java
public GptRequest()
```
Конструктор по умолчанию для JSON десериализации.

**Конструктор 2: С параметрами**
```java
public GptRequest(String description)
```
Удобный конструктор для создания экземпляров с описанием.

#### Пример использования
```java
GptRequest request = new GptRequest("деревянный обеденный стол с 4 стульями");
```

---

### 7. **MaterialRepository** (Доступ к данным)
**Пакет**: `com.daniyal.furniturebackend.repository`  
**Тип**: Spring Data JPA Repository интерфейс

#### Описание
Предоставляет методы доступа к базе данных для сущностей Material. Расширяет `JpaRepository` для автоматического наследования CRUD операций.

#### Аннотации
- `@Repository`: Помечает интерфейс как компонент репозитория

#### Наследуемые методы от JpaRepository

| Метод | Описание |
|-------|----------|
| `findAll()` | Возвращает все материалы из базы данных |
| `findById(Long id)` | Возвращает материал по ID |
| `save(Material material)` | Сохраняет или обновляет материал |
| `saveAll(List<Material> materials)` | Сохраняет несколько материалов |
| `delete(Material material)` | Удаляет материал |
| `deleteById(Long id)` | Удаляет материал по ID |
| `count()` | Возвращает общее количество материалов |

#### Пример использования
```java
List<Material> allMaterials = materialRepository.findAll();
Material saved = materialRepository.save(new Material());
```

---

### 8. **GptService** (Бизнес логика)
**Пакет**: `com.daniyal.furniturebackend.service`  
**Тип**: Сервис компонент

#### Описание
Основной сервис для интеграции GPT. Обрабатывает анализ мебели путем общения с API OpenAI, обработки ответов и возврата количеств материалов.

#### Аннотации
- `@Service`: Помечает класс как сервис компонент
- `@Value("${openai.api.key}")`: Внедряет ключ API OpenAI из конфигурации

#### Поля

| Поле | Тип | Назначение |
|------|-----|------------|
| `materialRepository` | MaterialRepository | Доступ к базе данных материалов |
| `apiKey` | String | Ключ аутентификации API OpenAI |

#### Основные методы

##### `analyzeFurniture(String userDescription): List<MaterialQuantity>`

**Назначение**: Основной метод для анализа мебели и извлечения необходимых материалов.

**Параметры**:
- `userDescription` (String): Описание мебели для анализа

**Возвращает**:
- `List<MaterialQuantity>`: Список материалов с требуемыми количествами

**Процесс**:
1. Получает все доступные материалы из базы данных
2. Строит промпт с описанием пользователя и списком материалов
3. Вызывает API OpenAI GPT
4. Парсит JSON ответ от GPT
5. Возвращает список объектов MaterialQuantity

**Бросает**: `RuntimeException` при любой ошибке во время анализа

**Пример**:
```java
List<MaterialQuantity> materials = gptService.analyzeFurniture("деревянный стол с ящиками");
```

---

##### `buildPrompt(String userRequest, List<Material> materials): String` (Приватный)

**Назначение**: Строит промпт, отправляемый в API GPT.

**Параметры**:
- `userRequest` (String): Описание мебели пользователя
- `materials` (List<Material>): Доступные материалы в базе данных

**Возвращает**:
- `String`: Форматированный промпт для API GPT

**Логика**:
- Форматирует доступные материалы как список
- Создает структурированный промпт с запросом пользователя и материалами
- Возвращает форматированную строку для использования API

---

##### `callGpt(String prompt): String` (Приватный)

**Назначение**: Делает HTTP запрос к API OpenAI и получает ответ.

**Параметры**:
- `prompt` (String): Промпт для отправки в GPT

**Возвращает**:
- `String`: Сырой JSON ответ от API GPT

**Детали API**:
- **Endpoint**: `https://api.openai.com/v1/chat/completions`
- **Модель**: `gpt-3.5-turbo`
- **Температура**: 0.1 (низкая креативность, последовательные результаты)
- **Формат ответа**: JSON объект

**Заголовки запроса**:
- `Authorization`: Bearer токен с ключом API
- `Content-Type`: application/json

**Обработка ошибок**: Бросает `RuntimeException` при неудаче вызова API

---

##### `parseResponse(String gptResponse): List<MaterialQuantity>` (Приватный)

**Назначение**: Парсит JSON ответ от GPT в объекты MaterialQuantity.

**Параметры**:
- `gptResponse` (String): Сырая JSON строка от API GPT

**Возвращает**:
- `List<MaterialQuantity>`: Распарсенные количества материалов

**Шаги обработки**:
1. Удаляет блоки кода markdown (```json, ```)
2. Использует Jackson ObjectMapper для десериализации JSON
3. Извлекает массив материалов из JSON
4. Маппит каждый материал в объект MaterialQuantity
5. Возвращает собранный список

**Ожидаемый формат JSON**:
```json
{
  "materials": [
    {"name": "Дерево", "quantity": 50.0},
    {"name": "Гвозди", "quantity": 100.0}
  ]
}
```

**Обработка ошибок**: Бросает `RuntimeException` при неудаче парсинга

---

## Конфигурация

### `src/main/resources/application.properties`

```properties
# Настройки приложения
spring.application.name=furniture-backend
server.port=8081

# Конфигурация логирования
logging.file.name=logs/furniture-backend.log
logging.file.path=./logs
logging.level.root=INFO
logging.level.org.springframework.web=INFO
logging.level.com.daniyal.furniturebackend=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30

# Конфигурация базы данных
spring.datasource.url=jdbc:postgresql://localhost:5432/furniture_db
spring.datasource.username=furniture_user
spring.datasource.password=2005
spring.datasource.driver-class-name=org.postgresql.Driver

# Конфигурация JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Конфигурация OpenAI API
openai.api.key=sk-proj-xxxxx...
openai.api.url=https://api.openai.com/v1/chat/completions
```

---

## Сводка API Endpoints

### Endpoints анализа мебели

| Метод | Endpoint | Описание | Запрос | Ответ |
|-------|----------|----------|--------|-------|
| POST | `/api/furniture/analyze` | Анализирует мебель и получает материалы | GptRequest | Список MaterialQuantity |

### Endpoints управления материалами

| Метод | Endpoint | Описание | Запрос | Ответ |
|-------|----------|----------|--------|-------|
| GET | `/api/materials` | Получить все материалы | N/A | Список Material |
| POST | `/api/materials` | Добавить один материал | Material | Material (с ID) |
| POST | `/api/materials/batch` | Добавить несколько материалов | List<Material> | List<Material> |

---

## Зависимости

### Основные зависимости (из pom.xml)
- **spring-boot-starter-data-jpa**: Поддержка JPA и Hibernate
- **spring-boot-starter-web**: Поддержка Web MVC и REST
- **postgresql**: JDBC драйвер PostgreSQL
- **lombok**: Обработка аннотаций для геттеров/сеттеров
- **jackson-databind**: Сериализация/десериализация JSON

---

## Начало работы

### Предварительные требования
- Java 21
- PostgreSQL 12+
- Maven 3.6+
- Ключ API OpenAI

### Инструкции по установке

1. **Клонировать репозиторий**
   ```bash
   git clone <url-репозитория>
   cd furniture-backend
   ```

2. **Настроить базу данных**
   - Обновить `src/main/resources/application.properties` с учетными данными PostgreSQL
   - Убедиться, что база данных `furniture_db` существует

3. **Настроить OpenAI API**
   - Обновить `openai.api.key` в `src/main/resources/application.properties`

4. **Собрать проект**
   ```bash
   mvn clean install
   ```

5. **Запустить приложение**
   ```bash
   mvn spring-boot:run
   ```
   Приложение запустится на `http://localhost:8081`

### Настройка начальных данных

Загрузить начальные материалы используя batch endpoint:
```bash
POST /api/materials/batch
```

---

## Обработка ошибок

### Общие ответы с ошибками

#### 400 Bad Request
- Неверный формат запроса
- Сбой анализа GPT
- Сбой операции базы данных

#### 500 Internal Server Error
- Проблемы подключения к БД
- OpenAI API недоступен
- Ошибки парсинга JSON

### Поток обработки исключений
1. Контроллеры ловят исключения
2. Форматируется ответ с ошибкой success=false
3. Сообщение об ошибке включается в тело ответа
4. Устанавливается соответствующий HTTP статус код

---

## Логирование

- **Расположение логов**: `logs/furniture-backend.log`
- **Уровень логирования**: DEBUG для кода приложения, INFO для фреймворков
- **Политика ротации**: Макс 10MB на файл, 30-дневная история
- **Паттерн**: `[timestamp] [thread] [level] [logger] - [message]`

---

## Технологии и версии

- Java: 21
- Spring Boot: 3.5.7
- PostgreSQL: Последняя совместимая версия
- Maven: 3.6+
- Lombok: (включен в Spring Boot parent)
- Jackson: (включен в Spring Boot parent)

---

## Замечания по разработке

### Конфигурация CORS
Все endpoints имеют CORS включенным для всех источников (`@CrossOrigin(origins = "*")`). Для продакшена ограничить конкретными доменами.

### Конфигурация GPT
- Модель: gpt-3.5-turbo (экономичная)
- Температура: 0.1 (детерминированные результаты)
- Формат ответа: Структурированный JSON

### Схема базы данных
Авто-генерируется Hibernate с `ddl-auto=update`. Начальное создание сущности Material автоматическое.

---

## Будущие улучшения

- Контроль доступа на основе ролей (RBAC)
- Отслеживание инвентаря материалов
- Интеграция расчета стоимости
- Многопользовательские проекты мебели
- Продвинутые стратегии кеширования
- Ограничение скорости API

---

## Лицензия

Этот проект предоставляется как есть для образовательных целей.

---

## Поддержка и контакты

По вопросам или проблемам обращайтесь к репозиторию проекта.
