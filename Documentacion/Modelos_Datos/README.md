# Modelos de datos — Edad del Perro v2.0

| Archivo | Contenido |
|---|---|
| `modelo_datos.png` | Modelo de datos (perro, tamaño, método, resultado, etapa) |
| `diagrama_clases.png` | Clases principales (lógica, ViewModels, almacenamiento) |
| `arquitectura.png` | Arquitectura por capas |
| `navegacion.png` | Navegación entre pestañas y formularios |
| `esquema_almacenamiento.json` | Formato JSON guardado en el celular, con ejemplo |
| `fuentes/*.mmd` | Código Mermaid editable de los diagramas |

## Diccionario de datos

### Dog — perro guardado (SharedPreferences `dogage_my_dogs` / `dogs_v1`)
| Campo | Tipo | Regla |
|---|---|---|
| id | Int | Único, autoincremental |
| name | String | 2–20 letras, espacios o guiones; no se repite |
| size | DogSize | SMALL, MEDIUM, LARGE o GIANT |
| years | Int | 0–30 |
| months | Int | 0–11; la edad total debe ser mayor a 0 |
| mode | CalcMode | SIMPLE, ADVANCED (por defecto) o SCIENTIFIC |

### DogSize — tamaño
| Valor | Etiqueta | Años humanos/año (desde el 3.º) | Senior desde | Esperanza de vida |
|---|---|---|---|---|
| SMALL | Pequeño | 4 | 10 años | 14 años |
| MEDIUM | Mediano | 5 | 9 años | 13 años |
| LARGE | Grande | 6 | 8 años | 11 años |
| GIANT | Gigante | 7 | 6 años | 9 años |

### CalcMode — método de cálculo
| Valor | Fórmula |
|---|---|
| SIMPLE | edad × 7 |
| ADVANCED | 1.er año = 15 · 2.º = 24 · luego 24 + tasa del tamaño × (edad − 2) |
| SCIENTIFIC | 16 · ln(edad) + 31 (menor a 1 año: 31 × edad) |

### LifeStage — etapa
| Valor | Regla |
|---|---|
| PUPPY | edad < 1 año |
| YOUNG | 1 ≤ edad < 3 |
| ADULT | 3 ≤ edad < senior del tamaño |
| SENIOR | edad ≥ senior del tamaño |

Para regenerar un diagrama: `mmdc -i fuentes/arquitectura.mmd -o arquitectura.png -s 2 -b white`.
