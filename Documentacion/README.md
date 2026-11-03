# Documentación — Edad del Perro v2.0

Documentación del proyecto según estándares IEEE / ISO. Cada documento está en Word (`.docx`, editable) y PDF.

```
Documentacion/
├── 01_Documentos_IEEE/
│   ├── SRS_Requisitos            IEEE 830 / ISO 29148 — requisitos
│   ├── Arquitectura_Diseno       IEEE 1016 / ISO 42010 — arquitectura y diseño
│   ├── Plan_Pruebas              IEEE 829 / ISO 29119-3 — plan de pruebas
│   └── Informe_Pruebas           IEEE 829 — informe resumen de pruebas
├── 02_Matrices/
│   ├── Matriz_Maestra.xlsx           requisitos, sprints, entregables y RACI
│   ├── Historias_Usuario.xlsx        11 historias con criterios de aceptación
│   ├── Trazabilidad_Requisitos.xlsx  RF → HU → componente → casos → pruebas unitarias
│   ├── Casos_Prueba.xlsx             24 casos funcionales + 26 pruebas unitarias
│   ├── Matriz_Riesgos.xlsx           8 riesgos con probabilidad × impacto y mapa de calor
│   ├── Cumplimiento_Guia.xlsx        lista de chequeo de entregables
│   └── Auditoria_Archivos.xlsx       inventario de archivos (nuevo/modificado)
├── 03_Manuales/
│   ├── Manual_Instalacion
│   ├── Manual_Tecnico
│   └── Manual_Usuario
├── 04_Casos_Uso_y_Prueba/
│   ├── Casos_Uso                       9 casos de uso (UML 2.5) con flujos
│   ├── Especificacion_Casos_Prueba     24 casos de prueba por caso de uso (IEEE 829)
│   ├── Matriz_CasosUso_CasosPrueba.xlsx
│   └── Diagramas/                      diagrama UML de casos de uso (PNG + SVG)
├── Modelos_Datos/                    modelo de datos, clases, arquitectura, navegación, esquema JSON
└── Documentacion_Almacenamiento      persistencia local de «Mis perros» (la app no usa Firebase)
```
