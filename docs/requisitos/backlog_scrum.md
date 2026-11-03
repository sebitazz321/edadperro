# Product Backlog Scrum — Aplicación de la edad del perro

Sprints de **2 semanas**. Estados del tablero: New → Approved → Committed → Done.

## Sprint 1: Diseño de interfaz y formulario

| ID | Product Backlog Item | Effort | Tasks (horas) |
|---|---|---|---|
| PBI-01 | Diseñar la interfaz principal de la calculadora | 5 | Bocetar pantallas (papel o Figma) (3 h)<br>Definir paleta de colores y tipografía (2 h)<br>Implementar tema Material 3 claro/oscuro (2 h) |
| PBI-02 | Crear el formulario de datos del perro | 5 | Selector de tamaño (chips) (3 h)<br>Campos de años y meses (3 h)<br>Selector de método de cálculo (2 h) |
| PBI-03 | Navegación por pestañas Calculadora / Chatbot | 3 | Implementar barra de navegación inferior (3 h) |

## Sprint 2: Implementación del cálculo y validaciones

| ID | Product Backlog Item | Effort | Tasks (horas) |
|---|---|---|---|
| PBI-04 | Calcular la edad humana con tres métodos | 8 | Implementar DogAgeCalculator (simple, avanzado, científico) (5 h)<br>Definir etapas de vida por tamaño (2 h) |
| PBI-05 | Validar las entradas del formulario | 5 | Implementar parseAge con reglas de validación (3 h)<br>Mostrar mensajes de error en pantalla (2 h) |
| PBI-06 | Mostrar resultado con etapa y progreso de vida | 5 | Tarjeta de resultado con animación (4 h)<br>Barra de vida recorrida (2 h) |
| PBI-07 | Gráfica comparativa perro vs. humano | 5 | Dibujar curva y ejes con Canvas (5 h) |
| PBI-08 | Chatbot Pepe con respuestas por reglas | 8 | Implementar DogBot (intenciones y extracción de edad/tamaño) (6 h)<br>Pantalla de chat con burbujas y sugerencias (4 h) |

## Sprint 3: Pruebas, correcciones y documentación

| ID | Product Backlog Item | Effort | Tasks (horas) |
|---|---|---|---|
| PBI-09 | Pruebas unitarias del cálculo, validaciones y chatbot | 5 | Escribir DogAgeCalculatorTest (3 h)<br>Escribir DogBotTest (3 h)<br>Ejecutar pruebas en el pipeline (1 h) |
| PBI-10 | Configurar pipeline de integración continua | 3 | Crear azure-pipelines.yml (2 h)<br>Ejecutar el pipeline y verificar APK (2 h) |
| PBI-11 | Documentar el proyecto | 3 | Completar README (integrantes, funcionalidades) (2 h)<br>Guardar evidencias y diagramas en docs/ (2 h) |

## Sprint 4: Mis perros y documentación

| ID | Product Backlog Item | Effort | Tasks (horas) |
|---|---|---|---|
| PBI-12 | Guardar perros en «Mis perros» | 5 | Modelo Dog, DogValidator y DogOps (4 h)<br>DogStore con SharedPreferences (2 h) |
| PBI-13 | Pantalla «Mis perros» | 8 | Lista con tarjetas, indicadores y estado vacío (5 h)<br>Formulario crear/editar y confirmación de borrado (3 h) |
| PBI-14 | Integración con la calculadora | 3 | Botón «Guardar en Mis perros» y «Calcular» desde la lista (3 h) |
| PBI-15 | Pruebas y documentación IEEE | 5 | DogProfilesTest (3 h)<br>Carpeta Documentacion (IEEE, matrices, manuales, casos de uso y prueba) (6 h) |

## Bugs de ejemplo

- Las opciones de tamaño se cortan en pantallas pequeñas (corregido con scroll horizontal)
