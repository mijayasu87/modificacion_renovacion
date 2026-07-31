# Playbook: implementar PRÓRROGAS en un proyecto estilo TRANSFERENCIAS (p. ej. RENOVACIÓN)

Este documento resume, paso a paso, cómo se implementó la funcionalidad **PRÓRROGAS** en el
proyecto de modificaciones al registro, para replicarla en un proyecto cuya estructura es
**similar a TRANSFERENCIAS** (tablas separadas por estado: `notificacion`, `abandono`,
`desistimiento`, `caducada`, ... en lugar de una sola tabla con `tipo_estado`).

> Reemplaza los nombres `Renovacion*` / `renovacion` por los reales del proyecto destino.
> Al abrir Claude Code en ese proyecto, podrá leer el código real y afinar los nombres exactos.

---

## 0. Concepto del flujo (idéntico al de abandono)

1. En la pantalla de **NOTIFICADAS**, el usuario **selecciona** registros ya emitidos
   (`notificacion_emitida = true`) y pulsa **PARA PRÓRROGA**.
2. Se abre un **diálogo** con los seleccionados; permite indicar **días de prórroga**
   (default **10**) y, **por fila**, `numero_alcance` (No. de escrito) y `fecha_alcance`.
3. Al confirmar, cada registro se marca en la **tabla de notificadas** con
   `fecha_puesta_prorroga`, `dias_prorroga`, `numero_alcance`, `fecha_alcance`
   (sigue en NOTIFICADAS, resaltado en ámbar, con conteo de días).
4. Un **scheduler** diario evalúa `fecha_puesta_prorroga + dias_prorroga` en **días hábiles**;
   al cumplirse, **copia** el registro a la tabla dedicada **`prorroga`** (fija `fecha_prorroga`
   y `numero_prorroga`) y **elimina** el registro de notificadas.
5. La tabla `prorroga` se lista en una **pestaña PRÓRROGAS** propia (buscar, editar, historial,
   expediente, eliminar, y **descarga de PDF** individual y múltiple).
6. Al **notificar** el PDF de la prórroga (subida de certificados), se marca
   `prorroga_notificada = true` en el registro de `prorroga`; en la pestaña, el **número de
   prórroga** se vuelve un **enlace** al PDF notificado.

**Reglas de negocio:**
- Solo se puede poner para prórroga lo que esté **notificado/emitido** (si no, alerta).
- **Exclusión mutua** con abandono: si está "para abandono" no se puede "para prórroga" y viceversa.
- `numero_prorroga`: **secuencia anual incremental**, obligatoria; la asigna el scheduler (o el
  guardar/editar si viniera nula).
- Para **ver el PDF** son indispensables `numero_prorroga`, `numero_alcance` y `fecha_alcance`
  (si faltan, se lanza aviso y no se abre).

---

## 1. Base de datos (las crea el usuario)

### 1.1 Tabla nueva `prorroga`
Crear una tabla `prorroga` con **la misma forma que la tabla `abandono`** del proyecto (todos los
campos base: solicitud, fecha_presentacion, notificacion, fecha_notificacion, registro,
fecha_registro, denominacion, signo, titulares/partes, apoderado, casilleros, responsable,
identificacion, email, comprobante, certificado, certificado_emitido, notificacion_emitida,
cancelado, solicitante, ...) **más** las columnas de prórroga:

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

### 1.2 Columnas "pendiente" en la tabla de notificadas
En la tabla que hace de **NOTIFICADAS** (equivalente a `notificacion` en transferencias):

```sql
ALTER TABLE <tabla_notificadas>
    ADD COLUMN fecha_puesta_prorroga DATE,
    ADD COLUMN dias_prorroga         INTEGER,
    ADD COLUMN numero_alcance        VARCHAR(255),
    ADD COLUMN fecha_alcance         DATE;
```

---

## 2. Entidades JPA

### 2.1 Entidad `Prorroga` (tabla `prorroga`)
Clonar la entidad `Abandono` (misma tabla-forma), renombrar `@Table(name="prorroga")`, clase
`Prorroga`, y añadir los campos + getters/setters:
`fechaPuestaProrroga`, `fechaProrroga`, `prorrogaNotificada` (Boolean), `diasProrroga` (Integer),
`numeroProrroga` (Integer), `numeroAlcance` (String), `fechaAlcance` (Date).
Registrar la clase en `persistence.xml` (mismo persistence-unit que las demás entidades del módulo).

### 2.2 Entidad de NOTIFICADAS
Añadir a la entidad de notificadas los 4 campos "pendiente" con sus getters/setters:
`fechaPuestaProrroga` (Date), `diasProrroga` (Integer), `numeroAlcance` (String),
`fechaAlcance` (Date).

---

## 3. DAOs

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

### 3.2 DAO de NOTIFICADAS — añadir candidatas
```java
public List<Notificada> getProrrogasCandidatas() {
    Query q = getEntityManager().createQuery(
        "SELECT n FROM Notificada n WHERE n.fechaPuestaProrroga IS NOT NULL");
    q.setHint("javax.persistence.cache.storeMode", "REFRESH");
    return q.getResultList();
}
```

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
getProrrogasCandidatas()        // desde el DAO de notificadas
```
Patrón de save/update/remove idéntico a `saveAbandono/updateAbandono/removeAbandono`
(el remove hace `merge` si el entity no está managed).

---

## 5. Bean de NOTIFICADAS — botón "PARA PRÓRROGA"
Agregar (junto a la lógica de abandono existente):

- Campo `private Integer diasProrroga;` + getter/setter.
- Helper `getSolicitudesParaProrroga()` → lista (coma-separada) de seleccionados con
  `fechaPuestaProrroga != null`.
- `prepararParaProrrogas()`:
  - valida selección no vacía;
  - valida que **todos** estén emitidos (`isNotificacionEmitida()`), si no → aviso listando;
  - valida que **ninguno** esté para abandono (`fechaPuestaAbandono != null`) → aviso "está para abandono";
  - avisa si alguno ya estaba para prórroga (se actualizarán días);
  - `diasProrroga = 10`; callback `proit=true` para abrir el diálogo.
- `paraProrrogas()`:
  - por cada seleccionado: re-valida emitido y no-abandono (red de seguridad);
  - `setFechaPuestaProrroga(new Date())`, `setDiasProrroga(diasProrroga)` (los `numeroAlcance`/
    `fechaAlcance` se bindean por fila en el diálogo);
  - `updateNotificada(...)` + `saveHistorial(... "PARA PRÓRROGA (N DÍAS)")`;
  - recarga y callback `proit=true`.
- **Exclusión mutua**: en `prepararParaAbandonos()` / `prepararPasarAbandonos()` agregar, antes de
  abrir sus diálogos, el chequeo `getSolicitudesParaProrroga()` → si no vacío, aviso
  "está para prórroga, no se puede para abandono". Y red de seguridad dentro de
  `paraAbandonos()`/`pasarAAbandonos()`.
- **Tooltip**: en `getTooltipAbandono(...)` (o el método de tooltip de fila), al inicio:
  ```java
  if (noti.getFechaPuestaProrroga() != null && noti.getDiasProrroga() != null) {
      LocalDate limite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(
          noti.getFechaPuestaProrroga(), noti.getDiasProrroga());
      long faltan = ChronoUnit.DAYS.between(LocalDate.now(), limite);
      return faltan >= 0
          ? "Faltan " + faltan + " días para pasar el trámite " + noti.getSolicitud() + " a prórroga"
          : "La prórroga del trámite " + noti.getSolicitud() + " ya venció hace " + Math.abs(faltan) + " días";
  }
  ```

---

## 6. Página de NOTIFICADAS (.xhtml)
- Botón **PARA PRÓRROGA** junto a PARA ABANDONO:
  `oncomplete="if(args.proit){PF('dlgParaProrroga').show();}"`, `update="mensajes dlgParaProrroga paraProForm"`.
- **Leyenda** "PARA PRÓRROGA" (caja ámbar) junto a las demás.
- **rowStyleClass**: prioriza prórroga:
  `#{notificacion.fechaPuestaProrroga ne null ? 'row-prorroga' : (<lo de abandono existente>)}`.
- **Enlace de menú** PRÓRROGAS → `prorrogaren.xhtml`.
- **Diálogo `dlgParaProrroga`** (form `paraProForm`): tabla de seleccionados con columnas
  Solicitud / Denominación / Notificación / F. Notificación / **Escrito No. (Alcance)** (inputText a
  `#{item.numeroAlcance}`) / **Fecha Escrito** (calendar a `#{item.fechaAlcance}`); debajo, input
  **Días de Prórroga** (`#{bean.diasProrroga}`, required); botones CERRAR y **PARA PRÓRROGA**
  (`actionListener=#{bean.paraProrrogas}`, `oncomplete="if(args.proit){PF('dlgParaProrroga').hide();}"`).

CSS de la fila (en style.css, si no existe):
```css
.row-prorroga { border: solid #ff9800 !important; background: #fff3e0 !important; }
```

---

## 7. Scheduler — pasar de notificadas a `prorroga`
En el singleton `@Schedule` que mueve abandonos, agregar `createProrrogasRenovacion()` y llamarlo:

```java
public void createProrrogasRenovacion() {
    Controlador c = new Controlador();
    List<Notificada> candidatas = c.getProrrogasCandidatas();
    for (Notificada notaux : candidatas) {
        if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) continue;
        LocalDate limite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(
            notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
        if (!LocalDate.now().isBefore(limite)) {         // hoy >= límite (días hábiles)
            Prorroga p = new Prorroga();
            // ... copiar TODOS los campos base desde notaux (igual que createAbandonos copia a Abandono)
            p.setFechaPuestaProrroga(notaux.getFechaPuestaProrroga());
            p.setDiasProrroga(notaux.getDiasProrroga());
            p.setNumeroAlcance(notaux.getNumeroAlcance());
            p.setFechaAlcance(notaux.getFechaAlcance());
            p.setFechaProrroga(new Date());
            p.setNumeroProrroga(c.getNextNumeroProrroga(new Date()));
            if (c.saveProrroga(p) && c.removeNotificada(notaux)) {
                c.saveHistorial("PRORROGA", "NOTIFICADAS", p.getSolicitud(), "PASADO A", 0, "modificaciones");
            }
        }
    }
}
```

---

## 8. Pestaña PRÓRROGAS: bean + página
- **`ProrrogaRenBean`** (clon del bean de una pestaña listado, tipo `ProrrogaTransfBean`):
  `loadProrrogas()` = `c.getProrrogas()`, `buscarProrroga` = byCriteria, `buscarProrrogasPorFecha`
  = byFecha, `eliminarProrroga` = removeProrroga, `prepararEditar`/`guardarProrroga`
  (autoasigna `numeroProrroga` si viene null), `prepararHistorial`, `buscarCasillero`,
  `prepararExpediente`, `selectedProrrogas`, `viewProrroga`/`downloadSelected` (flotante en LoginBean),
  `validarProrroga(...)` (busca el PDF notificado por `getUploadNotificacionBySolicitud` filtrando
  `tipo` que contenga `PRORROGA`), y `faltanDatosAlcance(...)` (exige numeroProrroga + numeroAlcance + fechaAlcance).
- **`prorrogaren.xhtml`** (clon de la pestaña de listado):
  tabla con columnas + **N. Prórroga como enlace** cuando `prorrogaNotificada` (abre el PDF con
  `otherpage(args.view)`), botón **Descargar** por fila (ícono PDF → servlet), botón **Descargar
  Seleccionados** dentro del `<f:facet name="footer">` de la tabla (¡importante que esté dentro para
  que `process="tablaDeDatos"` lo procese!), diálogo de edición con `Escrito No.`/`Fecha Escrito`/
  `N. Prórroga` (readonly). Campo `numeroProrroga` de solo-lectura.
  Enlaces de menú PRÓRROGAS en todas las páginas del módulo.

---

## 9. Reporte (PDF) descargable
- **LoginBean**: flotante `Prorroga getProrroga()/setProrroga()` + `List<Prorroga> getProrrogas()/setProrrogas()`.
- **Servlet `InformeProrrogaRen`** (clon de `InformeAbandono`/`InformeProrrogaTransf`),
  `urlPatterns={"/prorrogarenreport"}`; individual (lb.isVarious()==false → un PDF) y múltiple
  (zip). Reutiliza la plantilla `.jrxml` genérica pasando el `tipo_mod` = nombre de la tabla
  (`"prorroga"`).
- El método de `Report.java` que llena el reporte es genérico (recibe `tipo_mod`); reutilizar el
  mismo que usa abandono (`viewAbandonoProrrogaAll` / `...MasterBytes`).
- **OJO plantilla**: el `.jrxml` compartido hace `SELECT a.* FROM <tipo_mod>`; si declara campos que
  la tabla `prorroga` no tiene, Jasper falla al generar. Solución: agregar esas columnas a `prorroga`
  o hacer un `.jrxml` dedicado. Verificar al probar el PDF.

---

## 10. Marcar `prorroga_notificada` al notificar el PDF
En el bean/flujo de **subida y notificación de certificados** (equivalente a `UploadCertBean`),
en la rama de este módulo, tras el chequeo de abandono, agregar:

```java
Prorroga proaux = c.getProrrogaBySolicitud(un.getSolicitud());
if (proaux.getId() != null) {
    proaux.setProrrogaNotificada(true);
    c.updateProrroga(proaux);
    c.saveHistorial("PRORROGA", "PRORROGA", proaux.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
    un.setTipo("PRORROGA RENOVACION");
}
```
(Identifica por tabla: si la solicitud existe en `prorroga`, el documento notificado es la prórroga.
En los módulos de una sola tabla se usó detección por texto "ampliación al término"; en tablas
separadas basta con `getProrrogaBySolicitud`.)

---

## 11. Reporte de modificaciones (si el proyecto tiene un reporte consolidado)
Si existe un "reporte de modificaciones" que consolida estados: agregar `addProrroga(Prorroga)`
(estado "PRORROGA", numDocumento = numeroProrroga, fecha = fechaProrroga) y engancharlo en cada
ruta de búsqueda (por número, fecha, denominación, titular), consultando `getProrrogas...`.
Igual con abandono si faltara.

---

## 12. Presentación de diálogos (opcional, cosmético)
Clase CSS `dlgFormNice` (barra de título de color, contenido con mejor espaciado) aplicada como
`styleClass="dlgFormNice"` a los diálogos de formulario (`dlgNuevaT`). Se puede dar un color propio
por módulo con una clase extra (`dlgFormNice dlg-ren`) que sobreescriba
`.dlgFormNice.dlg-ren.ui-dialog .ui-dialog-titlebar { background: <color>; }` y el foco de inputs.

---

## Checklist de implementación

- [ ] BD: tabla `prorroga` (forma de `abandono` + 7 col. prórroga) + 4 col. "pendiente" en notificadas
- [ ] Entidad `Prorroga` + registro en `persistence.xml`
- [ ] 4 campos "pendiente" en entidad de notificadas
- [ ] `ProrrogaDAO` + `getProrrogasCandidatas()` en DAO de notificadas
- [ ] Wrappers en Controlador (save/update/remove/get.../numeración/validar/candidatas)
- [ ] Bean notificadas: diasProrroga, prepararParaProrrogas, paraProrrogas, exclusión mutua, tooltip
- [ ] Página notificadas: botón + diálogo + rowStyle + leyenda + enlace de menú
- [ ] Scheduler: `createProrrogas...` (copiar → `prorroga`, eliminar de notificadas)
- [ ] `ProrrogaRenBean` + `prorrogaren.xhtml` + enlaces de menú
- [ ] LoginBean flotante `Prorroga` + servlet `InformeProrrogaRen`
- [ ] Marcar `prorroga_notificada` en el flujo de notificación de PDFs
- [ ] (opcional) reporte consolidado: addProrroga
- [ ] (opcional) estilo `dlgFormNice` con color
- [ ] Compilar y probar: poner para prórroga → esperar/forzar scheduler → ver en pestaña → descargar PDF

---

### Notas clave aprendidas
- El plazo se calcula en **días hábiles** con `Operaciones.calcularFechaLimiteExcluyendoFinesSemana(Date, int)`.
- El **scheduler** persiste cada registro antes del siguiente, así la numeración anual no se repite.
- El botón "Descargar Seleccionados" **debe ir dentro del `<f:facet name="footer">`** de la tabla
  para que `process="tablaDeDatos"` lo ejecute (si va fuera, no dispara la acción ni muestra mensajes).
- En Java no confundir `&&` con la entidad HTML (`&amp;&amp;`).
