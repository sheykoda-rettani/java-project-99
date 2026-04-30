### Проект менеджер задач:
[![Actions Status](https://github.com/sheykoda-rettani/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/sheykoda-rettani/java-project-99/actions) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sheykoda-rettani_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=sheykoda-rettani_java-project-72)  [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=sheykoda-rettani_java-project-99&metric=bugs)](https://sonarcloud.io/summary/new_code?id=sheykoda-rettani_java-project-72)
<br/>Это веб-приложение, реализованное на языке Java с использованием фреймворка Spring Boot. Оно представляет собой полноценный таск-менеджер с возможностью управления задачами, статусами, метками и пользователями.

## Описание проекта

Проект реализует классическую CRUD (Create, Read, Update, Delete) логику для сущностей:
*   **Пользователи (Users):** Регистрация, вход (аутентификация), просмотр профиля.
*   **Задачи (Tasks):** Создание, редактирование, назначение исполнителей и меток.
*   **Статусы (Statuses):** Управление жизненным циклом задачи (Черновик, В работе, Завершено).
*   **Метки (Labels):** Категоризация задач (например, "Баг", "Фича").

## Установка и запуск проекта
В первую очередь надо сделать пару ключей rsa и поместить их в директорию `src/main/resources/certs`

Прежде всего установите утилиту `make`. Затем соберите проект:
```shell
  make build 
```

Чтобы запустить проект, используйте следующую команду:
```shell
  make run-dist 
```

## Развернутая версия приложения доступна по ссылке
[Менеджер задач](https://task-manager-jf5a.onrender.com)
