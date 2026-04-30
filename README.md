# 🎨 Artely App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)

## 📌 Introducción
**Artely** es una plataforma móvil diseñada para conectar a artistas independientes con amantes del arte. En un mercado donde la visibilidad es un reto, Artely ofrece un espacio dedicado donde los creadores pueden exponer su trabajo (galerías), y los usuarios pueden explorar, guardar sus obras favoritas e interactuar directamente con los artistas a través de un chat en tiempo real.

Este proyecto fue desarrollado con el objetivo de resolver la brecha de comunicación entre creadores y compradores, centralizando el descubrimiento de arte y la mensajería en una sola experiencia nativa, fluida y moderna.

---

## Características Principales
* **Gestión de Roles Integrada:** Soporte para dos tipos de cuentas (`Artista` y `Cliente/Espectador`), cada una con flujos y vistas de perfil personalizadas.
* **Autenticación Segura:** Inicio de sesión y registro gestionado mediante Firebase Authentication.
* **Exploración y Galerías:** Un feed principal donde los usuarios pueden descubrir nuevas obras de arte.
* **Interacción en Tiempo Real:** Sistema de mensajería (chat) instantáneo entre clientes y artistas impulsado por Firebase Realtime Database.
* **Notificaciones Push:** Integración de Cloud Functions y Firebase Cloud Messaging (FCM) para alertar a los usuarios sobre nuevos mensajes y chats, incluso cuando la app está en segundo plano.
* **Favoritos:** Capacidad para que los clientes guarden y gestionen las obras que más les gustan.

---

## Tecnologías y Herramientas Utilizadas

**Frontend (Android/Kotlin):**
* **Kotlin:** Lenguaje de programación principal.
* **Jetpack Compose:** Toolkit moderno para la construcción de interfaces de usuario (UI) de forma declarativa.
* **ViewModel & Coroutines/Flow:** Patrón de manejo de estado y asincronía.
* **Navigation Compose:** Gestión de rutas y navegación dentro de la app.
* **Coil:** Librería eficiente para la carga asíncrona del caché e imágenes.
* **DataStore:** Almacenamiento local de preferencias.

**Backend & Servicios (Firebase):**
* **Firebase Authentication:** Gestión de acceso y credenciales.
* **Firebase Realtime Database:** Base de datos NoSQL para almacenamiento de mensajes, usuarios, obras y sincronización en tiempo real.
* **Firebase Cloud Messaging (FCM):** Envío de notificaciones push.
* **Firebase Cloud Functions (Node.js/TypeScript):** Lógica sin servidor (serverless) ejecutada en backend para disparar notificaciones automáticamente al detectar cambios en la base de datos.

**Arquitectura:**
* **MVVM (Model-View-ViewModel):** Separación clara entre la lógica de negocio, los datos y la interfaz de usuario.
* **Patrón Repository:** Capa de abstracción para centralizar y aislar el acceso a Firebase de los ViewModels.

---

## 🛠️ El Proceso de Desarrollo
El proyecto fue construido adoptando un enfoque ágil y colaborativo. 
1. **Planificación y Diseño:** Definimos los perfiles de usuario, los modelos de datos de Firebase (árboles JSON para `users`, `chats`, `messages`, `artworks`) y la experiencia de usuario general.
2. **Implementación UI/UX:** Construimos una interfaz moderna y responsiva enteramente con Jetpack Compose, alejándonos de la antigua manipulación de XML.
3. **Integración Backend:** Conectamos los repositorios a Firebase Realtime Database, implementando listeners (Flows) para garantizar que los datos (como los mensajes del chat) se actualizaran en la pantalla sin necesidad de recargar.
4. **Notificaciones Automatizadas:** Para evitar que el cliente móvil realizara trabajo pesado o expusiera llaves de seguridad, desarrollamos Cloud Functions en TypeScript que monitorean nodos específicos en la base de datos y notifican a los usuarios correspondientes vía FCM.

---

## ¿Qué aprendimos en equipo?
Desarrollar Artely nos dejó valiosos aprendizajes tanto técnicos como de habilidades blandas:
* **Dominio de Jetpack Compose:** Pasamos de pensar en UI imperativa a declarativa, manejando eficientemente el estado (StateFlow) y las recomposiciones.
* **Estructura de Datos NoSQL:** Aprendimos a desnormalizar datos en Firebase Realtime Database para optimizar las consultas de lecturas y escuchas en tiempo real.
* **Backend Serverless (Cloud Functions):** Descubrimos cómo desplegar código en la nube con TypeScript que reacciona a eventos de nuestra base de datos para ejecutar tareas complejas como el envío de notificaciones push.
* **Colaboración en Git:** Mejoramos nuestras prácticas en el manejo de repositorios, control de versiones (uso adecuado del `.gitignore`), y resolución de conflictos al trabajar en múltiples módulos simultáneamente.

---

## 👥 El Equipo y Nuestros Roles
Detrás de **Artely** hay un equipo de tres personas, donde, además de escribir código y aportar al desarrollo general de la aplicación, cada uno asumió responsabilidades clave para llevar el proyecto a buen puerto:

* **[Brian Luis Ruiz Pérez (MrX-zeta)](https://github.com/MrX-zeta) — Líder de Proyecto y Backend:**
  Como líder del equipo, me encargué de definir la arquitectura, mantener la comunicación clara, organizar los tiempos y agilizar bloqueos técnicos. A nivel de desarrollo, diseñé la estructura de la base de datos NoSQL, construí el backend (Cloud Functions) y lideré gran parte de la experiencia de usuario (UX) junto con el testing general.

* **[Karolina Guadalupe Trujillo (KarolinaTrujillo)](https://github.com/KarolinaTrujillo) — Diseño y Frontend:**
  Karolina fue la encargada del aspecto visual del proyecto. Lideró el diseño de interfaces, el maquetado y el prototipado inicial. Además, trabajó activamente en la realización de pruebas (testing) para garantizar que la interfaz implementada en Compose fuera fiel al diseño original.

* **[Heber Alexander Escobar (HAEN23)](https://github.com/HAEN23) — Documentación y Prototipado:**
  Heber colaboró estrechamente con Karolina en la etapa de prototipado para aterrizar la idea inicial. Posteriormente, se encargó de estructurar y mantener toda la documentación técnica y narrativa del proyecto, asegurando que el código y la plataforma estuvieran bien respaldados.

---

## ⚙️ Cómo ejecutar el proyecto (Prerrequisitos)

Si eres un desarrollador (o reclutador) y deseas probar la aplicación localmente, sigue estos pasos:

### 1. Clonar el repositorio
```bash
git clone https://github.com/TuUsuario/Artely-App.git
cd Artely-App
```

### 2. Configurar Firebase
Por razones de seguridad, el archivo de configuración de Firebase (`google-services.json`) no se encuentra en el repositorio público.
1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Registra una aplicación Android con el Application ID: `com.luis.artelyapp`.
3. Descarga el archivo `google-services.json` y colócalo en el directorio `app/` del proyecto.
4. (Opcional) Si deseas probar las Cloud Functions, deberás hacer deploy en tu propio entorno Firebase usando la carpeta `functions/`.

### 3. Abrir en Android Studio
1. Abre **Android Studio** (versión recomendada: Hedgehog o superior).
2. Selecciona *File > Open* y elige la carpeta clonada.
3. Espera a que Gradle sincronice todas las dependencias.

### 4. Lanzar la aplicación
1. Conecta un dispositivo físico preconfigurado con opciones de desarrollador o inicia un emulador de Android (API 29+ recomendada).
2. Presiona el botón verde **Run (Shift + F10)** en Android Studio.

---

*Proyecto desarrollado con ❤️ y mucho código por el equipo de Artely.*


