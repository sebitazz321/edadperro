# Aplicación de la edad del perro

**Materia:** Optativa II — Universidad de San Buenaventura
**Docente:** Ing. Jairo Armando Salcedo Aranda
**Herramienta de gestión:** Azure DevOps (Scrum)

## Integrantes

| Nombre                      | Rol Scrum |
|-----------------------------|---|
| _(Sarah Isabella Peña)_     | Product Owner |
| _(Juan Carlos Diaz)_        | Scrum Master |
| _(Sebastian Grande Moreno)_ | Developer |

## Descripción

App Android (Kotlin + Jetpack Compose) que convierte la edad de un perro a años humanos con tres métodos de cálculo (simple, avanzado por tamaño y científico), muestra la etapa de vida, una gráfica comparativa y un chatbot asistente.

## Funcionalidades

- Selección de tamaño del perro (pequeño, mediano, grande, gigante).
- Ingreso de edad en años y meses con validaciones.
- Tres métodos de cálculo: Simple (×7), Avanzado (AKC por tamaño) y Científico (16·ln(edad)+31).
- Resultado animado con etapa de vida (cachorro, joven, adulto, senior) y % de vida recorrida.
- Gráfica de la curva perro vs. humano con el punto actual.
- Chatbot «Pepe»: entiende frases como «tiene 5 años y 3 meses, es grande», y responde sobre cuidados, vacunas, alimentación y esperanza de vida.
- **Mis perros (v2.0):** guarda varios perros con nombre, tamaño y edad; muestra su edad humana, etapa y % de vida; botones «+1 mes» y «🎂 +1 año», editar, eliminar y «🧮 Calcular» para abrirlo en la calculadora. Desde la calculadora también puedes «💾 Guardar en Mis perros». Indicadores: total, edad humana promedio, seniors y el mayor de la manada. Se guarda en el celular (sin internet ni cuentas).
- Modo claro/oscuro automático.

## Tecnologías

Kotlin · Jetpack Compose (Material 3) · ViewModel · JUnit 4 · Gradle (Kotlin DSL) · Azure Pipelines

## Organización Scrum

| Sprint | Actividades |
|---|---|
| Sprint 1 | Diseño de interfaz y formulario |
| Sprint 2 | Implementación del cálculo y validaciones |
| Sprint 3 | Pruebas, correcciones y documentación |
| Sprint 4 | Mis perros y documentación IEEE |

Backlog completo en [`docs/requisitos/backlog_scrum.md`](docs/requisitos/backlog_scrum.md).
El archivo [`backlog_azure_import.csv`](docs/requisitos/backlog_azure_import.csv) se puede importar en Azure Boards.

## Estructura del repositorio

```
EdadPerro/
├── Documentacion/     # documentos IEEE, matrices, manuales, modelos, casos de uso y prueba
├── docs/
│   ├── requisitos/    # requisitos y backlog
│   ├── diagramas/     # arquitectura
│   └── evidencias/    # capturas de la práctica
├── app/
│   └── src/
│       ├── main/      # código fuente (src/)
│       └── test/      # pruebas automatizadas (tests/)
├── README.md
└── azure-pipelines.yml
```

## Agente de IA «Pepe» (Google Gemini)

El chat es un **agente híbrido**: el motor local de reglas calcula los datos exactos de la app y **Google Gemini** (`gemini-3.5-flash-lite`) redacta la respuesta. Si la IA no está configurada o falla (sin internet, clave inválida, límite de uso), responde el motor local y se informa el motivo. Cada mensaje indica si viene de la IA (🤖) o del modo local (⚙️).

**Configurar la clave (no se sube a Git):**

1. Crea una clave gratis en https://aistudio.google.com/apikey
2. En `local.properties` (raíz del proyecto) agrega: `GEMINI_API_KEY=tu_clave`
3. **File → Sync Project with Gradle Files** y ▶ Run.

Detalles en `Documentacion/07_Agente_IA/`. Seguridad en `Documentacion/06_Seguridad/`.

## Cómo ejecutar

1. Abre **Android Studio** (Koala o superior) → *Open* → selecciona esta carpeta.
2. Espera la sincronización de Gradle (usa JDK 17).
3. Crea un emulador (API 26+) o conecta un celular y pulsa ▶ *Run*.

Pruebas unitarias (26): `./gradlew testDebugUnitTest` (Windows: `gradlew.bat testDebugUnitTest`).
APK de depuración: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/`.

## Ramas

- `main`: versión estable (protegida con Pull Request).
- `develop`: integración de funcionalidades.
- `feature/<nombre>`: desarrollo de nuevas funcionalidades.

## Chatbot «Pepe»

Asistente local basado en reglas: funciona sin internet y sin claves de API. Ver `logic/DogBot.kt`.
