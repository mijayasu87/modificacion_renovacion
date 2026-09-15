# Playbook: implementar PRÓRROGAS en un proyecto estilo TRANSFERENCIAS (p. ej. RENOVACIÓN)

Este documento resume, paso a paso, cómo se implementó la funcionalidad **PRÓRROGAS** en el
proyecto de modificaciones al registro, para replicarla en un proyecto cuya estructura es
**similar a TRANSFERENCIAS** (tablas separadas por estado: `notificacion`, `abandono`,
`desistimiento`, `caducada`, ... en lugar de una sola tabla con `tipo_estado`).

> Reemplaza los nombres `Renovacion*` / `renovacion` por los reales del proyecto destino.
> Al abrir Claude Code en ese proyecto, podrá leer el código real y afinar los nombres exactos.

---

## 0. Concepto del flujo

Pedido de la dirección requirente: la opción **"Para Prórroga"** vive en el módulo de
**ABANDONOS** (no en notificadas), porque es en esa etapa donde el analista determina la
procedencia de la prórroga.

1. En la pantalla de **ABANDONOS**, el usuario **selecciona** los registros y pulsa
   **PARA PRÓRROGA** (no se valida la notificación: los trámites llegan a abandonos desde
   notificadas, por lo que se entiende que ya fueron notificados).
2. Se abre un **diálogo** con los seleccionados; permite indicar **días de prórroga**
   (default **10**) y, **por fila**, `numero_alcance` (No. de escrito) y `fecha_alcance`.
3. Al confirmar, **cada registro pasa de inmediato al estado de prórroga**: se copia a la tabla
   dedicada **`prorroga`** (fijando `fecha_puesta_prorroga` = hoy, `dias_prorroga`,
   `numero_alcance`, `fecha_alcance`, `fecha_prorroga` y `numero_prorroga`) y se **elimina** de
   `abandono`. No interviene ningún scheduler.
4. Desde ese momento el sistema **contabiliza el plazo** según el tipo de trámite (**días
   laborables** si la solicitud es `SENADI-XXXX-XXXX`, **días de corrido** si es
   `IEPI-XXXX-XXXX`) y, en la pestaña
   **PRÓRROGAS**, muestra la **alerta** al vencer: fila en rojo, columna "Plazo"
   (`FALTAN N DÍAS` / `VENCE HOY` / `VENCIDA HACE N DÍAS`), tooltip y aviso al abrir la pestaña
   con el total de trámites vencidos.
5. Con esa alerta el analista revisa el expediente y, desde el diálogo de edición de la
   prórroga (**PASAR A**), lo remite a **CERTIFICADOS (RENOVACIONES)**, **NOTIFICADAS** o
   **ABANDONO**.
6. Al **notificar** el PDF de la prórroga (subida de certificados), se marca
   `prorroga_notificada = true` en el registro de `prorroga`; en la pestaña, el **número de
   prórroga** se vuelve un **enlace** al PDF notificado.

**Reglas de negocio:**
- Cualquier abandono seleccionado puede pasar a prórroga (sin validar la notificación).
- No se puede duplicar: si la solicitud ya existe en `prorroga`, se avisa y no se procesa.
- `numero_prorroga`: **secuencia anual incremental**, obligatoria; se asigna al pasar a prórroga
  (o al guardar/editar si viniera nula).
- Para **ver el PDF** son indispensables `numero_prorroga`, `numero_alcance` y `fecha_alcance`
  (si faltan, se lanza aviso y no se abre).

---

## 1. Base de datos (las crea el usuario)

### 1.1 Tabla nueva `prorroga`
Crear una tabla `prorroga` con **la misma forma que la tabla de notificadas** del proyecto (todos
los campos base: solicitud, fecha_presentacion, notificacion, fecha_notifica, registro_no,
fecha_registro, denominacion, signo, titulares/partes, apoderado, casilleros, responsable,
identificacion, comprobantes, certificado_emitido, notificacion_emitida, cancelado, solicitante,
tipo_abandono, ...) **más** las columnas de prórroga:

```sql
-- columnas propias de prórroga en la tabla dedicada
fecha_puesta_prorroga DATE,
fecha_prorroga        DATE,
prorroga_notificada   BOOLEAN,
dias_prorroga         INTEGER,
numero_prorroga       INTEGER,
numero_alcance        VARCHAR(255),
fecha_alcance         DATE
```

### 1.2 Columnas de captura en la tabla de ABANDONOS
En la tabla que hace de **ABANDONOS** (donde el analista marca la prórroga):

```sql
ALTER TABLE <tabla_abandonos>
    ADD COLUMN fecha_puesta_prorroga DATE,
    ADD COLUMN dias_prorroga         INTEGER,
    ADD COLUMN numero_alcance        VARCHAR(255),
    ADD COLUMN fecha_alcance         DATE;
```

> `numero_alcance` y `fecha_alcance` se capturan **por fila** en el diálogo (bindeados a la
> entidad de abandono) y se copian a `prorroga` al confirmar. Como el registro se mueve de
> inmediato, estas columnas no guardan estado permanente, pero son necesarias para el binding.

---

## 2. Entidades JPA

### 2.1 Entidad `Prorroga` (tabla `prorroga`)
Clonar la entidad de notificadas (misma tabla-forma), renombrar `@Table(name="prorroga")`, clase
`Prorroga`, y añadir los campos + getters/setters:
`fechaPuestaProrroga`, `fechaProrroga`, `prorrogaNotificada` (Boolean), `diasProrroga` (Integer),
`numeroProrroga` (Integer), `numeroAlcance` (String), `fechaAlcance` (Date).
Registrar la clase en `persistence.xml` (mismo persistence-unit que las demás entidades del módulo).

### 2.2 Entidad de ABANDONOS
Añadir a la entidad de abandonos los 4 campos de captura con sus getters/setters:
`fechaPuestaProrroga` (Date), `diasProrroga` (Integer), `numeroAlcance` (String),
`fechaAlcance` (Date).

---

## 3. DAO

### 3.1 `ProrrogaDAO` (nuevo) — clonar de `AbandonoDAO`
Métodos:
- `buscarTodos()` → `SELECT n FROM Prorroga n ORDER BY n.id DESC` (maxResults 300)
- `validarExistenciaProrroga(Prorroga n)` → misma solicitud, id distinto
- `getProrrogaByCriteria(String text)` → LIKE por solicitud/denominacion/titular
- `getProrrogaBySolicitud(String solicitud)`
- `getProrrogaByFecha(Date ini, Date fin)`
- `getProrrogasByDenominacion(String denominacion)`
- `getProrrogaByTitular(String titular)`
- `getNextNumeroProrroga(Date fecha)` → `SELECT MAX(n.numeroProrroga) ...`, con reinicio por año
  (comparar `fecha.getYear()` vs el año de la última `fechaProrroga`).

---

## 4. Controlador (fachada) — wrappers
Agregar:
```
saveProrroga / updateProrroga / removeProrroga
getProrrogas() (todos)
getProrrogasByCriteria / getProrrogasByFecha / getProrrogasByDenominacion / getProrrogasByTitular
getProrrogaBySolicitud
getNextNumeroProrroga(Date)
validarExistenciaProrroga(Prorroga)
```
Patrón de save/update/remove idéntico a `saveAbandono/updateAbandono/removeAbandono`
(el remove hace `merge` si el entity no está managed).

---

## 5. Bean de ABANDONOS — botón "PARA PRÓRROGA"
Agregar:

- Campo `private Integer diasProrroga;` + getter/setter.
- `prepararParaProrrogas()`:
  - valida selección no vacía;
  - `diasProrroga = 10`; callback `proit=true` para abrir el diálogo.
- `paraProrrogas(ActionEvent)`: por cada seleccionado
  - valida que **no exista ya** en `prorroga`;
  - crea `Prorroga` copiando **todos** los campos base (ojo con el mapeo de nombres:
    `registro`→`registroNo`, `fechaNotificacion`→`fechaNotifica`, `comprobante`→
    `noComprobantePresentSolic`, `certificado`(Integer)→`noComprobanteEmisionCert`(String),
    `fechaVencimiento`→`fechaVenceRegistro`);
  - fija `fechaPuestaProrroga = new Date()`, `diasProrroga`, `numeroAlcance`/`fechaAlcance`
    (capturados por fila), `fechaProrroga = new Date()`,
    `numeroProrroga = c.getNextNumeroProrroga(...)`, `prorrogaNotificada = false`;
  - `saveProrroga(p)` + `removeAbandono(abanaux)` + `saveHistorial(... "PASADO A PRÓRROGA (N DÍAS)")`;
  - recarga y callback `proit=true`.

---

## 6. Página de ABANDONOS (.xhtml)
- Botón **PARA PRÓRROGA** sobre la tabla:
  `process="@this tablaDeAbandonos"`, `update="mensajes allform:dlgParaProrroga allform:paraProForm"`,
  `oncomplete="if(args.proit){PF('dlgParaProrroga').show();}"`.
- **Diálogo `dlgParaProrroga`** (form `paraProForm`): tabla de seleccionados con columnas
  Solicitud / Denominación / Abandono / F. Abandono / **Escrito No. (Alcance)** (inputText a
  `#{item.numeroAlcance}`) / **Fecha Escrito** (calendar a `#{item.fechaAlcance}`); debajo, input
  **Días de Prórroga** (`#{bean.diasProrroga}`, required); botones CERRAR y **PARA PRÓRROGA**
  (`actionListener=#{bean.paraProrrogas}`, `oncomplete="if(args.proit){PF('dlgParaProrroga').hide();}"`).
- **Enlace de menú** PRÓRROGAS en todas las páginas del módulo.

---

## 7. Alerta de vencimiento (sin scheduler)
El plazo se calcula al vuelo en el bean de la pestaña PRÓRROGAS con
`Operaciones.calcularFechaLimiteSegunTramite(solicitud, fechaPuestaProrroga, diasProrroga)`, que
usa **días de corrido** cuando `esTramiteIepi(solicitud)` (prefijo `IEPI`) y **días laborables**
(sin fines de semana) en caso contrario (SENADI):

```java
public Long getDiasRestantes(Prorroga p)   // negativo = vencida
public boolean isVencida(Prorroga p)
public String  getEstadoPlazo(Prorroga p)  // "FALTAN N DÍAS" / "VENCE HOY" / "VENCIDA HACE N DÍAS"
public String  getTipoConteo(Prorroga p)   // "LABORABLES" (SENADI) | "DE CORRIDO" (IEPI)
public String  getDiasTexto(Prorroga p)    // "10 laborables" / "10 de corrido"
public String  getTooltipProrroga(Prorroga p)
public String  getEstiloFila(Prorroga p)   // 'row-prorroga' | 'row-prorroga-vencida'
public String  getClasePlazo(Prorroga p)   // 'plazo-vigente' | 'plazo-porvencer' | 'plazo-vencido'
public int     getNumeroVencidas()
public void    alertaVencidas()            // growl al abrir la pestaña (solo si !isPostback)
```

En la página: `<f:metadata><f:event type="preRenderView" listener="#{bean.alertaVencidas}"/></f:metadata>`
antes de `<h:head>`, `rowStyleClass="#{bean.getEstiloFila(prorroga)}"`, columnas
**F. Inicio Plazo / Días / Plazo** (con `title` = tooltip), leyendas EN PLAZO / PLAZO VENCIDO y
un rótulo con el conteo de vencidas.

CSS:
```css
.row-prorroga          { border: solid #ff9800 !important; background: #fff3e0 !important; }
.row-prorroga-vencida  { border: solid #e53935 !important; background: #ffebee !important; }
/* etiqueta de la columna "Plazo": píldora de color */
.plazo-vigente { background: #2e7d32 !important; }   /* holgura      */
.plazo-porvencer { background: #ef6c00 !important; } /* <= 2 días    */
.plazo-vencido { background: #c62828 !important; }   /* vencida      */
```

---

## 8. Pestaña PRÓRROGAS: bean + página
- **`ProrrogaBean`** (clon de un bean de pestaña listado): `loadProrrogas()`, `buscarProrroga`,
  `buscarProrrogasPorFecha`, `eliminarProrroga`, `prepararEditar`/`guardarProrroga`
  (autoasigna `numeroProrroga` si viene null), `prepararHistorial`, `buscarCasillero`,
  `prepararExpediente`, `selectedProrrogas`, `validarProrroga(...)` (busca el PDF notificado),
  `faltanDatosAlcance(...)`, `prepararDescarga`/`downloadSelected`, más los métodos de plazo del
  punto 7 y los de transferencia:
  `pasarARenovaciones` (CERTIFICADOS), `pasarANotificadas`, `pasarAAbandonos`.
- **`prorroga.xhtml`**: tabla con **N. Prórroga como enlace** cuando `prorrogaNotificada`,
  botón **Descargar** por fila, **Descargar Seleccionados** dentro del `<f:facet name="footer">`
  de la tabla, y diálogo de edición con `Escrito No.`/`Fecha Escrito`/`N. Prórroga` (readonly) y
  el combo **PASAR A**: `CERTIFICADOS (RENOVACIONES)` / `NOTIFICADAS` / `ABANDONO`.

---

## 9. Reporte (PDF) descargable
- **LoginBean**: flotante `Prorroga getProrroga()/setProrroga()` + `List<Prorroga> getProrrogas()/setProrrogas()`.
- **Servlet `InformeProrroga`** (clon de `InformeAbandono`), `urlPatterns={"/prorrogareport"}`;
  individual (`lb.isVarious()==false` → un PDF) y múltiple (zip). Reutiliza la plantilla `.jrxml`
  genérica pasando el `tipo_mod` = nombre de la tabla (`"prorroga"`).
- **OJO plantilla**: el `.jrxml` compartido hace `SELECT a.* FROM <tipo_mod>`; si declara campos que
  la tabla `prorroga` no tiene, Jasper falla al generar. Solución: agregar esas columnas a `prorroga`
  o hacer un `.jrxml` dedicado.

---

## 10. Marcar `prorroga_notificada` al notificar el PDF
En el bean/flujo de **subida y notificación de certificados** (equivalente a `UploadCertBean`),
en la rama de este módulo, agregar:

```java
Prorroga proaux = c.getProrrogaBySolicitud(un.getSolicitud());
if (proaux.getId() != null) {
    proaux.setProrrogaNotificada(true);
    c.updateProrroga(proaux);
    c.saveHistorial("PRORROGA", "PRORROGA", proaux.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
    un.setTipo("PRORROGA RENOVACION");
}
```

---

## 11. Reporte de modificaciones (si el proyecto tiene un reporte consolidado)
Si existe un "reporte de modificaciones" que consolida estados: agregar `addProrroga(Prorroga)`
(estado "PRORROGA", numDocumento = numeroProrroga, fecha = fechaProrroga) y engancharlo en cada
ruta de búsqueda (por número, fecha, denominación, titular).

---

## Checklist de implementación

- [ ] BD: tabla `prorroga` (forma de notificadas + 7 col. prórroga) + 4 col. de captura en abandonos
- [ ] Entidad `Prorroga` + registro en `persistence.xml`
- [ ] 4 campos de captura en la entidad de abandonos
- [ ] `ProrrogaDAO`
- [ ] Wrappers en Controlador (save/update/remove/get.../numeración/validar)
- [ ] Bean abandonos: diasProrroga, prepararParaProrrogas, paraProrrogas (copia + borra)
- [ ] Página abandonos: botón + diálogo + enlace de menú
- [ ] `ProrrogaBean` con plazo/alerta + `prorroga.xhtml` (columnas de plazo, leyendas, PASAR A)
- [ ] CSS `row-prorroga` / `row-prorroga-vencida`
- [ ] LoginBean flotante `Prorroga` + servlet `InformeProrroga`
- [ ] Marcar `prorroga_notificada` en el flujo de notificación de PDFs
- [ ] (opcional) reporte consolidado: addProrroga
- [ ] Compilar y probar: para prórroga desde abandonos → ver en pestaña con el conteo de días →
      esperar/forzar el vencimiento → alerta → remitir a certificados/notificadas/abandono → PDF

---

### Notas clave aprendidas
- El plazo se calcula con `Operaciones.calcularFechaLimiteSegunTramite(String, Date, int)`:
  **días laborables** para SENADI y **días de corrido** para IEPI (el reporte PDF también cambia
  de formato según ese prefijo).
- El paso a prórroga es **inmediato** (no hay scheduler): el scheduler solo mueve abandonos.
- Un botón que usa `process="tablaX"` **debe incluir `@this`** (`process="@this tablaX"`), si no su
  propia acción no se ejecuta; o bien colocarlo dentro del `<f:facet name="footer">` de la tabla.
- En Java no confundir `&&` con la entidad HTML (`&amp;&amp;`).
