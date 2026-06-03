/**
 * Módulo principal del FrontEnd para la gestión de atletas de Triatlón UD.
 * Maneja la comunicación con el microservicio MS1 (Competidor) mediante Fetch API,
 * y controla la visualización dinámica de tablas, tarjetas, carrusel de especialidades,
 * formularios de registro, modificacion, eliminacion y consulta de atletas.
 */

/**
 * URL base del microservicio MS1 (Competidor) en Spring Boot.
 * @constant {string}
 */
const API_URL = "http://localhost:9000/api/competidor";

/**
 * Lista global de atletas cargada desde el servidor.
 * @type {Array<Object>}
 */
let listaAtletas = [];

/**
 * Realiza una petición asíncrona al BackEnd MS1 para obtener todos los atletas.
 * Como el MS1 no expone un "traer todos", se consulta por los géneros existentes
 * y se unen los resultados en una sola lista. Refresca la interfaz y el contador.
 *
 * @async
 * @function obtenerAtletasDelServidor
 * @returns {Promise<void>}
 */
async function obtenerAtletasDelServidor(){
  try{
    //el MS1 consulta por filtros, se piden los dos generos y se unen las listas
    const respMasc = await fetch(`${API_URL}/consultargenero?genero=masculino`);
    const respFem  = await fetch(`${API_URL}/consultargenero?genero=femenino`);

    const masculinos = respMasc.ok ? await respMasc.json() : [];
    const femeninos  = respFem.ok  ? await respFem.json()  : [];

    listaAtletas = [...masculinos, ...femeninos];

    refrescarInterfaz();

    const countAtletas = document.getElementById('count-atletas');
    if (countAtletas) countAtletas.textContent = listaAtletas.length;
  }
  catch (error){
    mostrarMensaje('error', 'No se pudo conectar al servidor (MS1 - puerto 9000)');
  }
}

/**
 * Objeto que mapea cada especialidad de triatlón a una clase CSS de Bootstrap.
 * @constant {Object.<string, string>}
 */
const coloresEspecialidad = {
  'Super Sprint':     'border-success',
  'Sprint':           'border-warning',
  'Olimpica':         'border-info',
  'Media Distancia':  'border-secondary',
  'Larga Distancia':  'border-danger',
  'Ultraman':         'border-dark'
};

/**
 * Objeto que mapea cada especialidad de triatlón a un ícono de Bootstrap Icons.
 * @constant {Object.<string, string>}
 */
const iconosEspecialidad = {
  'Super Sprint':   'bi-wind',
  'Sprint': 'bi-lightning',
  'Olimpica':  'bi-bicycle',
  'Media Distancia': 'bi-person-walking',
  'Larga Distancia':  'bi-person-arms-up',
  'Ultraman':  'bi-fire'
};

/**
 * Calcula la categoría de competencia de un atleta según su edad.
 *
 * @function calcularCategoria
 * @param {number|string} edad - Edad del atleta.
 * @returns {string} Nombre de la categoría correspondiente a la edad.
 */
function calcularCategoria(edad) {
  edad = parseInt(edad);
  if (edad === 7)          return 'Pre-benjamin';
  if (edad <= 9)           return 'Benjamin';
  if (edad <= 11)          return 'Alevin';
  if (edad <= 13)          return 'Infantil';
  if (edad <= 15)          return 'Cadetes';
  if (edad <= 17)          return 'Juvenil';
  if (edad <= 19)          return 'Junior';
  if (edad <= 23)          return 'Sub-23';
  if (edad <= 39)          return 'Absoluta';
  if (edad >= 40)          return 'Veteranos';
  return 'Sin categoria';
}

/**
 * Oculta los paneles de confirmación de eliminación y modificación en listado.html.
 *
 * @function ocultarSecciones
 * @returns {void}
 */
function ocultarSecciones() {
  const eliminar    = document.getElementById('contenedor-eliminar');
  const actualizar  = document.getElementById('contenedor-actualizar');
  if (eliminar)  eliminar.classList.add('d-none');
  if (actualizar) actualizar.classList.add('d-none');
}

/**
 * Muestra una alerta flotante en la esquina superior derecha de la pantalla.
 *
 * @function mostrarMensaje
 * @param {string} tipo - 'exitoso', 'warning', 'error', 'sucess'.
 * @param {string} texto - Texto que se mostrará dentro de la alerta.
 * @returns {void}
 */
function mostrarMensaje(tipo, texto) {
  const clases = {
    'exitoso': 'alert-success',
    'warning': 'alert-warning',
    'error':   'alert-danger',
    'sucess':  'alert-success'
  };

  const claseAlerta = clases[tipo] || 'alert-info';
  const alerta = document.createElement('div');
  alerta.className = `alert ${claseAlerta} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
  alerta.style.zIndex = 9999;
  alerta.innerHTML = `
        ${texto}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
  document.body.appendChild(alerta);

  setTimeout(() => alerta.remove(), 3000);
}

/**
 * Variable global que almacena la especialidad actualmente seleccionada en el carrusel.
 * @type {string}
 */
let especialidadActiva = 'Todas';

/**
 * Crea y renderiza el carrusel horizontal de especialidades en listado.html.
 *
 * @function CrearCarrusel
 * @returns {void}
 */
function CrearCarrusel(){
  const seguimiento = document.getElementById('carrusel-t');
  if (!seguimiento) return;

  const especialidades = ['todas', ...new Set (listaAtletas.map(a => a.especialidad))];
  seguimiento.innerHTML = '';
  especialidades.forEach(esp => {
    const cantidad = esp === 'todas'
      ? listaAtletas.length
      : listaAtletas.filter(a => a.especialidad === esp).length;
    const colorBorde = coloresEspecialidad[esp]|| 'border-primary';
    const icono = iconosEspecialidad[esp]|| 'bi-star';
    const activo = especialidadActiva === esp ? 'border-3 shadow-sm' : '';
    seguimiento.innerHTML += `
        <div class="card text center p-2 flex-shrink-0 ${colorBorde} ${activo}"
            style="min-width:120px; cursor:pointer; border-top: 4px solid; border-top-color: inherit;"
            onclick="filtrarPorEspecialidad('${esp}')"
            id="esp-card-${esp}">
            <i class="bi ${icono} fs-4 mb-1"></i>
            <div class="fw-semibold small">${esp}</div>
            <div class="text-muted" style="font-size:0.75rem">${cantidad} atleta${cantidad !== 1 ? 's' : ''}</div>
        </div>`;
  });
}

/**
 * Filtra la tabla y las tarjetas de atletas según la especialidad seleccionada.
 *
 * @function filtrarPorEspecialidad
 * @param {string} esp - Nombre de la especialidad seleccionada.
 * @returns {void}
 */
function filtrarPorEspecialidad(esp){
  especialidadActiva = esp;
  const lista = esp === 'todas'
    ? listaAtletas
    : listaAtletas.filter(a => a.especialidad === esp);
  CrearTabla(lista);
  CrearTarjetas(lista);
  CrearCarrusel();
}

/**
 * Recarga toda la interfaz visual con los datos actuales de listaAtletas.
 *
 * @function refrescarInterfaz
 * @returns {void}
 */
function refrescarInterfaz() {
  CrearCarrusel();
  if (document.getElementById('tabla-atletas-body')) CrearTabla(listaAtletas);
  if (document.getElementById('vista-tarjetas'))     CrearTarjetas(listaAtletas);
}

/**
 * Genera y renderiza la tabla HTML de atletas en listado.html.
 *
 * @function CrearTabla
 * @param {Array<Object>} listaAtletas - Array de objetos atleta a renderizar.
 * @returns {void}
 */
function CrearTabla(listaAtletas){
  const cuerpoTabla = document.getElementById('tabla-atletas-body');
  if (!cuerpoTabla) return;
  cuerpoTabla.innerHTML="";

  listaAtletas.forEach(atleta => {
    const fila = document.createElement('tr');
    fila.innerHTML=`
        <td> <img src="${atleta.foto || 'https://cdn-icons-png.flaticon.com/512/1077/1077114.png'}" width="40" height="40" class="rounded-circle" style="object-fit:cover;" onerror="this.src='https://cdn-icons-png.flaticon.com/512/1077/1077114.png'"></td>
        <td> ${atleta.identificacion}</td>
        <td>${atleta.nombre}</td>
        <td><span class="badge bg-info text-dark">${atleta.categoria}</span></td>
        <td>${atleta.especialidad}</td>
        <td class="text-end">
            <button class="btn btn-sm btn-warning" onclick="prepararModificacion(${atleta.id})">
                <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-danger" onclick="prepararEliminacion(${atleta.id})">
                <i class="bi bi-trash"></i>
            </button>
        </td>
        `;
    cuerpoTabla.appendChild(fila);
  });
}

/**
 * Genera y renderiza las tarjetas visuales de atletas en listado.html.
 *
 * @function CrearTarjetas
 * @param {Array<Object>} listaAtletas - Array de objetos atleta a renderizar como tarjetas.
 * @returns {void}
 */
function CrearTarjetas(listaAtletas){
  const contenedor = document.getElementById('vista-tarjetas');
  if (!contenedor) return;
  contenedor.innerHTML ="";

  listaAtletas.forEach(atleta=>{
    const colorClase = coloresEspecialidad[atleta.especialidad] || 'border-primary';
    contenedor.innerHTML +=`
        <div class="col-3">
            <div class="card h-100 card-atleta shadow-sm border-top ${colorClase} border-4">
                <img src="${atleta.foto || 'https://cdn-icons-png.flaticon.com/512/1077/1077114.png'}" class="card-img-top foto-atleta" alt="${atleta.nombre}" style="height:200px; object-fit:cover;" onerror="this.src='https://cdn-icons-png.flaticon.com/512/1077/1077114.png'">
                <div class="card-body text-center">
                    <span class="badge rounded-pill bg-light text-primary mb-2 border border-primary">
                        ${atleta.categoria}
                    </span>
                    <h5 class="card-title fw-bold mb-1">${atleta.nombre}</h5>
                    <p class="small text-muted mb-3">ID:${atleta.identificacion}</p>
                    <div class="d-flex justify-content-center gap-2">
                        <button class="btn btn-sm btn-outline-warning" onclick="prepararModificacion(${atleta.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="prepararEliminacion(${atleta.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="card-footer bg-white border-0 text-center pb-3">
                    <small class="text-uppercase fw-bold text-secondary" style="font-size: 0.7rem;">
                        Especialidad: ${atleta.especialidad}
                    </small>
                </div>
            </div>
        </div>
        `;
  });
}

/**
 * Prepara y muestra el panel de confirmación de eliminación de un atleta.
 *
 * @function prepararEliminacion
 * @param {number} idRecibido - ID del atleta que se desea eliminar.
 * @returns {void}
 */
function prepararEliminacion(idRecibido){
  const atletaEncontrado = listaAtletas.find(a=> a.id === idRecibido);

  if (atletaEncontrado){
    document.getElementById('eliminar-nombre-texto').innerText = atletaEncontrado.nombre;
    document.getElementById('eliminar-id-texto').innerText = atletaEncontrado.identificacion;

    document.getElementById('contenedor-eliminar').classList.remove('d-none');
    document.getElementById('contenedor-actualizar').classList.add('d-none');

    const btnFinal = document.getElementById('btn-confirmar-borrado');
    btnFinal.onclick =() => ejecutarEliminacionReal(idRecibido);

    document.getElementById('contenedor-eliminar').scrollIntoView({behavior: 'smooth'});
  }
}

/**
 * Ejecuta la eliminación definitiva de un atleta llamando al endpoint DELETE del MS1.
 *
 * @async
 * @function ejecutarEliminacionReal
 * @param {number} idABorrar - ID del atleta que se eliminará.
 * @returns {Promise<void>}
 */
async function ejecutarEliminacionReal(idABorrar){
  try {
    const respuesta = await fetch(`${API_URL}/eliminar/${idABorrar}`, {
      method: 'DELETE'
    });
    if (respuesta.ok) {
      ocultarSecciones();
      await obtenerAtletasDelServidor();
      mostrarMensaje('exitoso','atleta eliminado');
    } else {
      mostrarMensaje('error', 'No se pudo eliminar el atleta');
    }
  } catch (error) {
    mostrarMensaje('error', 'No se pudo conectar al servidor (MS1)');
  }
}

/**
 * Prepara y muestra el formulario de modificación con los datos actuales del atleta.
 *
 * @function prepararModificacion
 * @param {number} idRecibido - ID del atleta cuyos datos se van a modificar.
 * @returns {void}
 */
function prepararModificacion(idRecibido){
  const atleta = listaAtletas.find(a => a.id === idRecibido);

  if (atleta){
    document.getElementById('modificacion-id-original').value = atleta.id;
    document.getElementById('modificacion-nombre').value = atleta.nombre;
    document.getElementById('modificacion-identificacion').value = atleta.identificacion;
    document.getElementById('modificacion-categoria').value = atleta.categoria;
    document.getElementById('modificacion-especialidad').value = atleta.especialidad;

    document.getElementById('contenedor-actualizar').classList.remove('d-none');
    document.getElementById('contenedor-eliminar').classList.add('d-none');
    document.getElementById('contenedor-actualizar').scrollIntoView({behavior: 'smooth'});
  }
}

/**
 * Renderiza la tabla de resultados en consulta.html con la lista de atletas recibida.
 *
 * @function consultarAtletas
 * @param {Array<Object>} listaAtletas - Array de atletas a mostrar en la tabla.
 * @returns {void}
 */
function consultarAtletas(listaAtletas) {
  const cuerpo = document.getElementById('tabla-consulta-body');
  if (!cuerpo) return;
  cuerpo.innerHTML = '';
  const sinResultados = document.getElementById('mensaje-sin-resultados');

  if (listaAtletas.length === 0) {
    if (sinResultados) sinResultados.classList.remove('d-none');
  } else {
    if (sinResultados) sinResultados.classList.add('d-none');
    listaAtletas.forEach(atleta => {
      const fila = document.createElement('tr');
      fila.innerHTML = `
                <td>${atleta.identificacion}</td>
                <td>${atleta.nombre}</td>
                <td>${atleta.edad || '—'}</td>
                <td>${atleta.genero || '—'}</td>
                <td><span class="badge bg-info text-dark">${atleta.categoria}</span></td>
                <td>${atleta.especialidad}</td>
                <td>${atleta.modalidadCross ? '<span class="badge bg-success">Sí</span>' : '<span class="badge bg-secondary">No</span>'}</td>
            `;
      cuerpo.appendChild(fila);
    });
  }
}

/**
 * Lee los valores de los filtros de consulta.html y consulta al MS1 según el criterio.
 * Cada filtro llama a su endpoint independiente del MS1 (genero, categoria,
 * especialidad o cross). El buscar por id usa el endpoint consultarcompetidor.
 *
 * @async
 * @function aplicarFiltros
 * @returns {Promise<void>}
 */
async function aplicarFiltros() {
  const buscarId     = document.getElementById('buscar-id')?.value.trim();
  const genero       = document.getElementById('filtro-genero')?.value;
  const categoria    = document.getElementById('filtro-categoria')?.value;
  const especialidad = document.getElementById('filtro-especialidad')?.value;
  const cross        = document.getElementById('filtro-cross')?.value;

  try {
    //buscar por id tiene prioridad, usa el endpoint puntual del MS1
    if (buscarId) {
      const resp = await fetch(`${API_URL}/consultarcompetidor/${buscarId}`);
      if (resp.ok) {
        const atleta = await resp.json();
        consultarAtletas([atleta]);
      } else {
        consultarAtletas([]);
      }
      return;
    }

    //cada filtro llama a su endpoint independiente del MS1
    let url = null;
    if (genero)            url = `${API_URL}/consultargenero?genero=${encodeURIComponent(genero)}`;
    else if (categoria)    url = `${API_URL}/consultarcategoria?categoria=${encodeURIComponent(categoria)}`;
    else if (especialidad) url = `${API_URL}/consultarespecialidad?especialidad=${encodeURIComponent(especialidad)}`;
    else if (cross !== '' && cross != null) url = `${API_URL}/consultarcross?cross=${cross}`;

    if (url) {
      const resp = await fetch(url);
      const lista = resp.ok ? await resp.json() : [];
      consultarAtletas(lista);
    } else {
      consultarAtletas(listaAtletas);
    }
  } catch (error) {
    mostrarMensaje('error', 'No se pudo conectar al servidor (MS1)');
  }
}

/**
 * Listener del formulario de registro de atletas.
 * Construye el objeto competidor con los valores del formulario, calcula la categoría
 * según la edad, lee la especialidad del carrusel y lo envía vía POST al MS1.
 */
const formRegistro = document.getElementById('form-registro');

if (formRegistro){
  formRegistro.addEventListener('submit', async function(e){
    e.preventDefault();

    //se arma el objeto con los nombres de campo que espera el MS1 (CompetidorDTO)
    const correoInput = document.getElementById('correo');
    const nuevoCompetidor ={
      nombre:         document.getElementById('nombre').value,
      identificacion: parseInt(document.getElementById('identificacion').value),
      edad:           parseInt(document.getElementById('edad').value),
      genero:         document.getElementById('genero').value,
      categoria:      calcularCategoria(document.getElementById('edad').value),
      correo:         correoInput ? correoInput.value : 'sincorreo@triatlon.com',
      foto:           document.getElementById('foto').value,
      especialidad:   document.getElementById('input-especialidad').value,
      modalidadCross: document.getElementById('esCross').checked
    };

    try {
      const respuesta = await fetch(`${API_URL}/crearcompetidor`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoCompetidor)
      });

      if (respuesta.ok) {
        formRegistro.reset();
        mostrarMensaje('sucess', 'Se guardo el atleta nuevo correctamente');
        if (typeof refrescarInterfaz === "function") refrescarInterfaz();
      } else {
        const errorTexto = await respuesta.text();
        mostrarMensaje('error', errorTexto || 'Error al crear el atleta');
      }
    } catch (error) {
      mostrarMensaje('error', 'No se pudo conectar al servidor (MS1 - puerto 9000)');
    }
  });
}

/**
 * Evento principal que se ejecuta cuando el navegador termina de cargar el HTML.
 * Inicializa todos los listeners de las páginas listado.html, consulta.html y registro.html.
 *
 * @event DOMContentLoaded
 */
document.addEventListener('DOMContentLoaded', () => {

  obtenerAtletasDelServidor();

  const btnTabla = document.getElementById('vista-table-btn');
  const btnTarjetas = document.getElementById('vista-cards-btn');
  const vistaTabla = document.getElementById('vista-tabla');
  const vistaTarjetas = document.getElementById('vista-tarjetas');

  /**
   * Cambia la vista entre tabla y tarjetas en listado.html.
   *
   * @function cambiarVista
   * @param {string} vista - 'tabla' o cualquier otro valor para tarjetas.
   * @returns {void}
   */
  function cambiarVista(vista){
    if (vista === 'tabla'){
      vistaTabla.classList.remove('d-none');
      vistaTarjetas.classList.add('d-none');
      btnTabla.classList.add('active');
      btnTarjetas.classList.remove('active');
    }else{
      vistaTabla.classList.add('d-none');
      vistaTarjetas.classList.remove('d-none');
      btnTabla.classList.remove('active');
      btnTarjetas.classList.add('active');
    }
  }
  if (btnTabla) btnTabla.addEventListener('click', () => cambiarVista('tabla'));
  if (btnTarjetas) btnTarjetas.addEventListener('click', () => cambiarVista('tarjetas'));

  const inputEdad = document.getElementById('edad');
  if (inputEdad) {
    inputEdad.addEventListener('input', function() {
      const categoria = calcularCategoria(this.value);
      const indicador = document.getElementById('categoria-calculada');
      if (indicador && this.value >= 7) {
        indicador.textContent = 'Categoría: ' + categoria;
        indicador.classList.remove('d-none');
      }
    });
  }

  // Listeners de consulta — solo si estamos en consulta.html
  const btnBuscarId = document.getElementById('btn-buscar-id');
  const btnLimpiar = document.getElementById('btn-limpiar-filtros');
  const filtroGenero = document.getElementById('filtro-genero');
  const filtroCategoria = document.getElementById('filtro-categoria');
  const filtroEspecialidad = document.getElementById('filtro-especialidad');
  const filtroCross = document.getElementById('filtro-cross');

  if (btnBuscarId) {
    btnBuscarId.addEventListener('click', aplicarFiltros);
  }
  if (btnLimpiar) {
    btnLimpiar.addEventListener('click', () => {
      document.getElementById('buscar-id').value = '';
      document.getElementById('filtro-genero').value = '';
      document.getElementById('filtro-categoria').value = '';
      document.getElementById('filtro-especialidad').value = '';
      if (filtroCross) filtroCross.value = '';
      consultarAtletas(listaAtletas);
    });
  }
  //los selects consultan al MS1 en tiempo real al cambiar
  if (filtroGenero)       filtroGenero.addEventListener('change', aplicarFiltros);
  if (filtroCategoria)    filtroCategoria.addEventListener('change', aplicarFiltros);
  if (filtroEspecialidad) filtroEspecialidad.addEventListener('change', aplicarFiltros);
  if (filtroCross)        filtroCross.addEventListener('change', aplicarFiltros);

  if (document.getElementById('tabla-consulta-body')) {
    consultarAtletas(listaAtletas);
  }

  /**
   * Listener del formulario de modificación. Detecta qué campos cambiaron respecto
   * al atleta original y llama a los endpoints PATCH correspondientes del MS1.
   */
  const formActualizar = document.getElementById('form-actualizar');
  if (formActualizar) {
    formActualizar.addEventListener('submit', async function(e) {
      e.preventDefault();
      const id = parseInt(document.getElementById('modificacion-id-original').value);
      const original = listaAtletas.find(a => a.id === id);
      if (!original) return;

      const nuevoNombre = document.getElementById('modificacion-nombre').value;
      const nuevaId     = document.getElementById('modificacion-identificacion').value;
      const nuevaCat    = document.getElementById('modificacion-categoria').value;

      try {
        //solo se llama al endpoint del campo que cambio
        if (nuevoNombre !== original.nombre) {
          await fetch(`${API_URL}/modificarnombre/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nuevoNombre })
          });
        }
        if (parseInt(nuevaId) !== original.identificacion) {
          await fetch(`${API_URL}/modificaridentificacion/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ identificacion: parseInt(nuevaId) })
          });
        }
        if (nuevaCat !== original.categoria) {
          await fetch(`${API_URL}/modificarcategoriaedad/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ categoriaEdad: nuevaCat })
          });
        }

        ocultarSecciones();
        await obtenerAtletasDelServidor();
        mostrarMensaje('warning', 'Atleta actualizado correctamente');
      } catch (error) {
        mostrarMensaje('error', 'No se pudo conectar al servidor (MS1)');
      }
    });
  }

  // ===== Carrusel de especialidades con flechas (registro.html) =====
  const especialidades = [
    { nombre: 'Super Sprint',    icono: 'bi-wind',          distancias: '350m · 10km · 2.5km',   color: '#1d9e75' },
    { nombre: 'Sprint',          icono: 'bi-lightning',     distancias: '750m · 20km · 5km',      color: '#ef9f27' },
    { nombre: 'Olimpica',        icono: 'bi-trophy',        distancias: '1.5km · 40km · 10km',    color: '#378add' },
    { nombre: 'Media Distancia', icono: 'bi-person-walking',distancias: '1.9km · 90km · 21km',    color: '#7f77dd' },
    { nombre: 'Larga Distancia', icono: 'bi-fire',          distancias: '3.8km · 180km · 42km',   color: '#d85a30' },
    { nombre: 'Ultraman',        icono: 'bi-shield',        distancias: '10km · 415km · 85km',    color: '#2c2c2a' },
  ];

  const cardEsp    = document.getElementById('esp-card-registro');
  const dotsEsp    = document.getElementById('esp-dots');
  const btnEspPrev = document.getElementById('btn-esp-prev');
  const btnEspNext = document.getElementById('btn-esp-next');

  if (cardEsp) {
    let actualEsp = 0;
    let seleccionadaEsp = null;

    function renderEsp() {
      const esp = especialidades[actualEsp];
      const esSel = seleccionadaEsp === actualEsp;

      cardEsp.innerHTML = `
            <i class="bi ${esp.icono} fs-1 mb-2" style="color:${esp.color}"></i>
            <h5 class="fw-bold">${esp.nombre}</h5>
            <p class="text-muted small">${esp.distancias}</p>
            ${esSel
        ? `<span class="badge bg-warning text-dark"><i class="bi bi-check-circle"></i> Seleccionada</span>`
        : `<button type="button" class="btn btn-sm btn-outline-secondary mt-1" id="btn-elegir-esp">Elegir esta</button>`
      }
        `;

      dotsEsp.innerHTML = '';
      especialidades.forEach((_, i) => {
        const dot = document.createElement('div');
        dot.style.cssText = `width:8px;height:8px;border-radius:50%;background:${i === actualEsp ? '#8c1919' : '#ccc'};transition:all 0.2s;`;
        dotsEsp.appendChild(dot);
      });

      btnEspPrev.disabled = actualEsp === 0;
      btnEspNext.disabled = actualEsp === especialidades.length - 1;

      const btnElegir = document.getElementById('btn-elegir-esp');
      if (btnElegir) {
        btnElegir.addEventListener('click', () => {
          seleccionadaEsp = actualEsp;
          document.getElementById('input-especialidad').value = esp.nombre;
          const indicador = document.getElementById('especialidad-seleccionada');
          const texto     = document.getElementById('texto-especialidad');
          if (indicador && texto) {
            texto.textContent = esp.nombre;
            indicador.classList.remove('d-none');
          }
          renderEsp();
        });
      }
    }

    btnEspPrev.addEventListener('click', () => { if (actualEsp > 0)                       { actualEsp--; renderEsp(); } });
    btnEspNext.addEventListener('click', () => { if (actualEsp < especialidades.length - 1) { actualEsp++; renderEsp(); } });

    renderEsp();
  }

});
