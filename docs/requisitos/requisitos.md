# Requisitos — Aplicación de la edad del perro

## Requisitos funcionales

| ID | Descripción |
|---|---|
| RF-01 | El usuario selecciona el tamaño del perro. |
| RF-02 | El usuario ingresa la edad en años y meses. |
| RF-03 | El sistema valida la entrada (solo números, meses 0-11, máximo 30 años, edad > 0). |
| RF-04 | El sistema calcula la edad humana equivalente con el método elegido. |
| RF-05 | El sistema muestra la etapa de vida y el porcentaje de vida recorrida. |
| RF-06 | El sistema muestra una gráfica comparativa perro vs. humano. |
| RF-07 | El usuario conversa con el chatbot para consultar edad equivalente y cuidados. |
| RF-08 | El usuario guarda perros en «Mis perros» con nombre, tamaño y edad (validados, sin nombres repetidos, máximo 20). |
| RF-09 | El usuario consulta la lista de sus perros con edad humana, etapa, % de vida e indicadores. |
| RF-10 | El usuario edita, elimina o actualiza la edad (+1 mes / +1 año) de un perro guardado. |
| RF-11 | El usuario abre un perro guardado en la calculadora o guarda el cálculo actual como perro. |

## Requisitos no funcionales

| ID | Descripción |
|---|---|
| RNF-01 | La app funciona sin conexión a internet. |
| RNF-02 | El resultado se actualiza en tiempo real al cambiar un dato. |
| RNF-03 | Compatible con Android 8.0 (API 26) o superior. |
| RNF-04 | Soporta modo claro y oscuro. |
| RNF-05 | «Mis perros» se guarda localmente (SharedPreferences) y se conserva al cerrar la app. |
