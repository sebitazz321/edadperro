# Arquitectura — Aplicación de la edad del perro

Patrón: UI declarativa (Jetpack Compose) + ViewModel + lógica pura en Kotlin (fácil de probar).

```mermaid
flowchart LR
    UI[Jetpack Compose UI] --> CALC[DogAgeCalculator]
    UI --> VM[ChatViewModel]
    UI --> DVM[DogsViewModel]
    VM --> BOT[DogBot]
    BOT --> CALC
    DVM --> OPS[DogValidator / DogOps]
    OPS --> CALC
    DVM --> STORE[(DogStore - SharedPreferences)]
```

Diagramas completos en `Documentacion/Modelos_Datos/`.
