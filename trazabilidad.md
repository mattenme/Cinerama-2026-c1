# Paso 5: Trazabilidad Completa — Cinerama

---

## 1. Cadena de Trazabilidad (RF → HU → CU → RNF → RN)

Cada fila muestra cómo un **RF** (Requerimiento Funcional) genera una **HU** (Historia de Usuario), se implementa mediante un **CU** (Caso de Uso), está sujeto a **RNF** (Requerimientos No Funcionales) y obedece **RN** (Reglas de Negocio).

| RF | HU | CU | RNF | RN |
|----|----|----|-----|----|
| RF-01 | HU-01 | CU-01 | RNF-10, RNF-25, RNF-31 | RN-11 |
| RF-02 | HU-02 | — | RNF-14, RNF-29 | RN-19, RN-29 |
| RF-03 | HU-03 | — | RNF-03, RNF-06, RNF-07 | — |
| RF-04 | HU-04 | CU-02 | RNF-01, RNF-06, RNF-07 | RN-14, RN-22 |
| RF-05 | HU-05 | CU-02 | RNF-11, RNF-12 | RN-03, RN-06, RN-31 |
| RF-06 | HU-06 | CU-02 | RNF-07 | RN-36 |
| RF-07 | HU-07 | CU-02 | RNF-10 | RN-10, RN-16, RN-33 |
| RF-08 | HU-08 | CU-05 | RNF-02, RNF-28 | RN-01, RN-12 |
| RF-09 | HU-09 | CU-05 | RNF-07, RNF-10 | RN-33, RN-36 |
| RF-10 | HU-10 | — | RNF-07 | RN-24, RN-30, RN-32 |
| RF-11 | HU-11 | — | RNF-06 | RN-15 |
| RF-12 | HU-12 | — | RNF-10 | — |
| RF-13 | RF-13 | — | RNF-29, RNF-30 | RN-19, RN-29 |
| RF-14 | HU-13 | CU-03 | RNF-05, RNF-08 | RN-05 |
| RF-15 | HU-14 | CU-04 | RNF-11 | RN-04, RN-34, RN-35 |
| RF-16 | HU-15 | — | RNF-03 | RN-21 |
| RF-17 | — | — | RNF-08 | RN-18 |
| RF-18 | HU-17 | — | RNF-10 | RN-16 |
| RF-19 | HU-16 | — | RNF-07 | RN-24, RN-30 |
| RF-20 | — | — | RNF-07 | — |
| RF-21 | — | — | RNF-01 | RN-13, RN-20 |
| RF-22 | — | — | RNF-06 | — |
| RF-23 | HU-18 | CU-06 | RNF-10 | RN-17 |
| RF-24 | HU-18 | — | RNF-03 | — |

---

## 2. Matriz Detallada por Componente

### 2.1 Registro y Autenticación

| Elemento | Detalle |
|----------|---------|
| **RF-01** | Registrarse como cliente con DNI, nombre, email y teléfono |
| **HU-01** | Yo como cliente, necesito registrarme con mi DNI y datos personales, para poder comprar entradas |
| **CU-01** | Registrar Cliente |
| **RNF-10** | Mensajes de error claros en español |
| **RNF-25** | Codificación UTF-8 |
| **RNF-31** | Almacenamiento en BD Oracle |
| **RN-11** | DNI hasta 20 caracteres |
| **Criterio aceptación** | CA-01: DNI nuevo → registro exitoso. CA-02: DNI duplicado → error |

### 2.2 Compra de Entradas (Flujo completo)

| Elemento | Detalle |
|----------|---------|
| **RF-03, RF-04, RF-05, RF-06, RF-07, RF-08, RF-09** | Ver cartelera, seleccionar asientos, agregar comida, aplicar promoción, pagar |
| **HU-03 a HU-09** | Historias de cliente desde ver cartelera hasta pagar |
| **CU-02** | Comprar Entradas |
| **CU-05** | Pagar Compra |
| **RNF-01** | Mapa de butacas en ≤2s |
| **RNF-02** | Pago responde en ≤3s |
| **RNF-11** | Transacción atómica |
| **RNF-12** | Timeout de bloqueo 3 min |
| **RN-01** | Pago antes de marcar vendida |
| **RN-03** | Liberar asientos al salir |
| **RN-06** | Timeout automático |
| **RN-12** | Métodos de pago válidos |
| **RN-30** | Mismo QR para grupo |
| **RN-31** | Bloquear asiento al seleccionar |
| **RN-32** | Precio total en primer asiento |
| **RN-33** | Descuento: `precio × (1 - desc/100)` |
| **RN-36** | Suma de productos |
| **RN-37** | Generación de QR |
| **Criterio aceptación** | CA-03: Timeout libera asientos. CA-04: Compra de 3 asientos = 1 fila. CA-05: Validar campos vacíos. CA-06: Código promocional |

### 2.3 Administración de Películas

| Elemento | Detalle |
|----------|---------|
| **RF-14** | CRUD de películas con imagen |
| **HU-13** | Yo como administrador, necesito registrar películas con imagen |
| **CU-03** | Gestionar Películas |
| **RNF-05** | Redimensionar imagen a 1200×1200 |
| **RNF-08** | Notificación toast al guardar |
| **RN-05** | Subir imagen antes de guardar |
| **Criterio aceptación** | CA-07: Archivo .txt rechazado. CA-08: Login admin con DNI 00000000 |

### 2.4 Administración de Salas

| Elemento | Detalle |
|----------|---------|
| **RF-15** | CRUD de salas con generación de butacas |
| **HU-14** | Yo como administrador, necesito crear salas con butacas automáticas |
| **CU-04** | Gestionar Salas con Butacas |
| **RNF-11** | Transacción atómica (sala + butacas) |
| **RN-04** | Butacas creadas inmediatamente después de sala |
| **RN-13** | Estados válidos de butaca |
| **RN-20** | UNIQUE (id_sala, fila, numero) |
| **RN-34** | Filas = techo(capacidad / 10) |
| **RN-35** | Letra de fila secuencial A=0 |
| **Criterio aceptación** | CA-10: Sala de capacidad 30 genera butacas A1..A10, B1..B10, C1..C10 |

### 2.5 Administración de Funciones

| Elemento | Detalle |
|----------|---------|
| **RF-16** | CRUD de funciones |
| **HU-15** | Yo como administrador, necesito asignar horarios a cada película |
| **RNF-03** | Carga en ≤2s |
| **RN-14** | Estados válidos de función |
| **RN-21** | FK a película y sala |

### 2.6 Reporte de Incidencias

| Elemento | Detalle |
|----------|---------|
| **RF-23** | Reportar incidencias |
| **HU-18** | Yo como operador, necesito reportar una incidencia técnica |
| **CU-06** | Reportar Incidencia |
| **RNF-10** | Mensajes claros |
| **RN-17** | Tipo de incidencia válido |
| **RN-26** | FK opcionales con ON DELETE SET NULL |

---

## 3. Matriz de Cobertura (RF vs. Código Fuente)

| RF | Archivo(s) clave |
|----|------------------|
| RF-01 | `ClienteController.java`, `registrarse.html`, `registros.js` |
| RF-02 | `ClienteController.java`, `login.html`, `login.js` |
| RF-03 | `FuncionController.java`, `cartelera.html`, `cartelera.js` |
| RF-04 | `ButacaController.java`, `asientos.html`, `asientos.js` |
| RF-05 | `ButacaController.java`, `asientos.js` |
| RF-06 | `ProductoController.java`, `asientos.html`, `asientos.js` |
| RF-07 | `PromocionController.java`, `pago.js` |
| RF-08 | `CompraController.java`, `pago.html`, `pago.js` |
| RF-09 | `pago.js` (resumen en pantalla) |
| RF-10 | `CompraController.java`, `perfil.js` |
| RF-11 | `CalificacionController.java`, `perfil.js` |
| RF-12 | `ClienteController.java`, `perfil.js` |
| RF-13 | `ClienteController.java`, `indexAdmin.html`, `admin.js` |
| RF-14 | `PeliculaController.java`, `peliculas.html`, `peliculas.js` |
| RF-15 | `SalaController.java`, `ButacaController.java`, `salas.html`, `salas.js` |
| RF-16 | `FuncionController.java`, `horarios.html`, `horarios.js` |
| RF-17 | `ProductoController.java`, `productos.html`, `productos.js` |
| RF-18 | `PromocionController.java`, `promociones.html` (admin), `promociones.js` |
| RF-19 | `CompraController.java`, `compras.html`, `compras.js` |
| RF-20 | `IncidenciaController.java`, `incidencias.html`, `incidencias.js` |
| RF-21 | `ButacaController.java`, `butacas.html`, `butacas.js` |
| RF-22 | `PeliculaController.java`, `index.html`, `indexAdmin.html`, `indexAdmin.js` |
| RF-23 | `IncidenciaController.java`, `incidencias.html`, `incidencias.js` |
| RF-24 | `FuncionController.java`, `horarios.html` |

---

## 4. Integración de RN en Casos de Uso

### CU-01: Registrar Cliente (con RN)

```
1. [RN-11] El cliente ingresa DNI (máx 20 caracteres)
2. El sistema valida que el DNI no exista
3. [RN-29] Si el DNI es "00000000", se rechaza (reservado para admin)
4. El sistema inserta el cliente
```

### CU-02: Comprar Entradas (con RN)

```
1. [RN-21] El sistema carga la función con su película y sala
2. [RN-22] El sistema carga las butacas de la sala
3. [RN-31] El cliente selecciona asientos → estado "Seleccionada"
4. [RN-06] Inicia timer de 3 min para liberación automática
5. [RN-36] El cliente agrega productos de comida
6. [RN-33] El cliente aplica código promocional (descuento)
7. [CU-05: Pagar]
   a. [RN-12] El cliente elige método de pago válido
   b. [RN-37] Se genera código QR de 8 caracteres
   c. [RN-32] Se asigna precio total al primer asiento, 0 al resto
   d. [RN-01] Se ejecuta transacción: INSERT Compra + UPDATE Butaca
   e. [RN-30] Todos los asientos comparten el mismo QR
8. [RN-03] Si el usuario cierra la página, se liberan los asientos
```

### CU-03: Gestionar Películas (con RN)

```
1. [RN-05] El administrador sube imagen y llena datos
2. [RNF-05] El sistema redimensiona la imagen a 1200×1200
3. El sistema guarda la película
```

### CU-04: Gestionar Salas (con RN)

```
1. El administrador ingresa nombre, tipo, capacidad
2. [RN-04] Se guarda la sala
3. [RN-34] Se calculan filas: Math.ceil(capacidad / 10)
4. [RN-35] Se asignan letras A, B, C...
5. [RN-20] Cada butaca con UNIQUE (id_sala, fila, numero)
6. [RN-13] Estado inicial "Disponible"
```

### CU-06: Reportar Incidencia (con RN)

```
1. [RN-17] El operador selecciona tipo válido
2. Ingresa descripción
3. [RN-26] Opcionalmente asocia a sala, función o cliente
```

---

## 5. Resumen de Trazabilidad

| Artefacto | Cantidad |
|-----------|----------|
| Requerimientos Funcionales (RF) | 24 |
| Historias de Usuario (HU) | 18 |
| Casos de Uso (CU) | 6 |
| Criterios de Aceptación | 10 |
| Requerimientos No Funcionales (RNF) | 35 |
| Reglas de Negocio (RN) | 37 |

Todos los elementos están conectados: cada **RF** tiene su **HU**, se implementa en uno o más **CU**, cumple **RNF** medibles y obedece **RN** atómicas.
