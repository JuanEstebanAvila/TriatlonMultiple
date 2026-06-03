/**
 * Módulo de gestión de carreras de triatlón.
 * Maneja la comunicación con el microservicio MS2 (Carrera) en puerto 9001
 * mediante Fetch API para operaciones CRUD sobre carreras.
 */

/**
 * URL base del microservicio MS2 (Carrera).
 * @constant {string}
 */
const API_CARRERAS = "http://localhost:9001/api/carrera";

/**
 * Lista global de carreras cargada desde MS2.
 * @type {Array<Object>}
 */
let listaCarreras = [];

/**
 * Oculta los paneles de eliminar y modificar carrera.
 * @function ocultarPanelesCar
 * @returns {void}
 */
function ocultarPanelesCar() {
  const panelEliminar  = document.getElementById('contenedor-eliminar-car');
  const panelModificar = document.getElementById('contenedor-modificar-car');
  if (panelEliminar)  panelEliminar.classList.add('d-none');
  if (panelModificar) panelModificar.classList.add('d-none');
}

/**
 * Muestra una alerta flotante en la esquina superior derecha.
 *
 * @function mostrarMensajeCar
 * @param {string} tipo - 'exitoso', 'warning' o 'error'
 * @param {string} texto - Texto a mostrar
 * @returns {void}
 */
function mostrarMensajeCar(tipo, texto) {
  const clases = {
    'exitoso': 'alert-success',
    'warning': 'alert-warning',
    'error':   'alert-danger'
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
 * Convierte el nivel de dificultad a estrellas visuales.
 * Si el nivel es un texto (Alta, Media, etc.) lo muestra tal cual.
 *
 * @function renderEstrellas
 * @param {number|string} nivel - Nivel del 1 al 5 o texto
 * @returns {string} Estrellas en HTML o el texto recibido
 */
function renderEstrellas(nivel) {
  const numero = parseInt(nivel);
  if (isNaN(numero)) return nivel || '—';
  const llenas = '⭐'.repeat(numero);
  const vacias = '☆'.repeat(Math.max(0, 5 - numero));
  return llenas + vacias;
}

/**
 * Obtiene una carrera por su id del servidor MS2 y refresca la tabla.
 * El MS2 no expone "traer todas", por eso al cargar la pantalla se deja
 * la tabla vacia hasta que el usuario busque por id o cree una carrera.
 *
 * @async
 * @function obtenerCarreras
 * @returns {Promise<void>}
 */
async function obtenerCarreras() {
  //el MS2 no tiene un endpoint "todas", se muestra lo que haya en la lista local
  renderizarTablaCarreras(listaCarreras);
  const contador = document.getElementById('count-carreras');
  if (contador) contador.textContent = listaCarreras.length;
}

/**
 * Renderiza la tabla HTML con la lista de carreras recibida.
 *
 * @function renderizarTablaCarreras
 * @param {Array<Object>} lista - Array de carreras a renderizar
 * @returns {void}
 */
function renderizarTablaCarreras(lista) {
  const cuerpo = document.getElementById('tabla-carreras-body');
  const sinResultados = document.getElementById('mensaje-sin-carreras');
  if (!cuerpo) return;
  cuerpo.innerHTML = '';

  if (lista.length === 0) {
    if (sinResultados) sinResultados.classList.remove('d-none');
    return;
  }
  if (sinResultados) sinResultados.classList.add('d-none');

  lista.forEach(car => {
    const fila = document.createElement('tr');
    fila.innerHTML = `
            <td><span class="badge bg-secondary">${car.id}</span></td>
            <td class="fw-bold">${car.nombreCarrera}</td>
            <td class="text-muted small">${car.ubicacion}</td>
            <td class="small">${car.fechaEjecucion || '—'}</td>
            <td>${renderEstrellas(car.nivelDificultad)}</td>
            <td class="text-muted small">${car.paraQuien || '—'}</td>
            <td class="text-end">
                <button class="btn btn-sm btn-warning me-1"
                        onclick="prepararModificacionCar(${car.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger"
                        onclick="prepararEliminacionCar(${car.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
    cuerpo.appendChild(fila);
  });
}

/**
 * Muestra el panel de confirmación de eliminación.
 *
 * @function prepararEliminacionCar
 * @param {number} id - ID de la carrera a eliminar
 * @returns {void}
 */
function prepararEliminacionCar(id) {
  const car = listaCarreras.find(c => c.id === id);
  if (!car) return;

  document.getElementById('eliminar-car-nombre').innerText    = car.nombreCarrera;
  document.getElementById('eliminar-car-ubicacion').innerText = car.ubicacion;
  document.getElementById('contenedor-eliminar-car').classList.remove('d-none');
  document.getElementById('contenedor-modificar-car').classList.add('d-none');

  document.getElementById('btn-confirmar-borrado-car').onclick = () => eliminarCarrera(id);
  document.getElementById('contenedor-eliminar-car').scrollIntoView({ behavior: 'smooth' });
}

/**
 * Ejecuta la eliminación de una carrera llamando al endpoint DELETE de MS2.
 *
 * @async
 * @function eliminarCarrera
 * @param {number} id - ID de la carrera a eliminar
 * @returns {Promise<void>}
 */
async function eliminarCarrera(id) {
  try {
    const respuesta = await fetch(`${API_CARRERAS}/eliminar/${id}`, {
      method: 'DELETE'
    });
    if (respuesta.ok) {
      mostrarMensajeCar('exitoso', 'Carrera eliminada correctamente');
      //se quita de la lista local y se refresca
      listaCarreras = listaCarreras.filter(c => c.id !== id);
      ocultarPanelesCar();
      await obtenerCarreras();
    } else if (respuesta.status === 404) {
      mostrarMensajeCar('error', 'La carrera no existe');
    } else {
      mostrarMensajeCar('error', 'Error al eliminar la carrera');
    }
  } catch (error) {
    mostrarMensajeCar('error', 'No se pudo conectar con MS2 (puerto 9001)');
  }
}

/**
 * Muestra el panel de modificación precargado con los datos actuales.
 *
 * @function prepararModificacionCar
 * @param {number} id - ID de la carrera a modificar
 * @returns {void}
 */
function prepararModificacionCar(id) {
  const car = listaCarreras.find(c => c.id === id);
  if (!car) return;

  document.getElementById('mod-car-id').value        = car.id;
  document.getElementById('mod-car-ubicacion').value = car.ubicacion     || '';
  document.getElementById('mod-car-fecha').value     = car.fechaEjecucion || '';

  document.getElementById('contenedor-modificar-car').classList.remove('d-none');
  document.getElementById('contenedor-eliminar-car').classList.add('d-none');
  document.getElementById('contenedor-modificar-car').scrollIntoView({ behavior: 'smooth' });
}

/**
 * Envía el PATCH de ubicación al endpoint de MS2.
 * El MS2 espera un cuerpo JSON con la clave "ubicacion".
 *
 * @async
 * @function modificarUbicacion
 * @returns {Promise<void>}
 */
async function modificarUbicacion() {
  const id    = document.getElementById('mod-car-id').value;
  const valor = document.getElementById('mod-car-ubicacion').value.trim();
  if (!valor) { mostrarMensajeCar('warning', 'Ingrese una ubicación'); return; }

  try {
    const respuesta = await fetch(`${API_CARRERAS}/modificarubicacion/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ubicacion: valor })
    });
    if (respuesta.ok) {
      const actualizada = await respuesta.json();
      //se actualiza la carrera en la lista local
      const i = listaCarreras.findIndex(c => c.id === parseInt(id));
      if (i !== -1) listaCarreras[i] = actualizada;
      mostrarMensajeCar('exitoso', 'Ubicación actualizada');
      ocultarPanelesCar();
      await obtenerCarreras();
    } else {
      mostrarMensajeCar('error', 'Error al actualizar ubicación');
    }
  } catch (error) {
    mostrarMensajeCar('error', 'No se pudo conectar con MS2 (puerto 9001)');
  }
}

/**
 * Envía el PATCH de fecha al endpoint de MS2.
 * El MS2 espera un cuerpo JSON con la clave "fechaEjecucion" (numero).
 *
 * @async
 * @function modificarFecha
 * @returns {Promise<void>}
 */
async function modificarFecha() {
  const id    = document.getElementById('mod-car-id').value;
  const valor = document.getElementById('mod-car-fecha').value;
  if (!valor) { mostrarMensajeCar('warning', 'Seleccione una fecha'); return; }

  try {
    const respuesta = await fetch(`${API_CARRERAS}/modificarfecha/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fechaEjecucion: parseInt(valor) })
    });
    if (respuesta.ok) {
      const actualizada = await respuesta.json();
      const i = listaCarreras.findIndex(c => c.id === parseInt(id));
      if (i !== -1) listaCarreras[i] = actualizada;
      mostrarMensajeCar('exitoso', 'Fecha actualizada');
      ocultarPanelesCar();
      await obtenerCarreras();
    } else {
      mostrarMensajeCar('error', 'Error al actualizar fecha');
    }
  } catch (error) {
    mostrarMensajeCar('error', 'No se pudo conectar con MS2 (puerto 9001)');
  }
}

/**
 * Evento principal — inicializa listeners cuando el DOM está listo.
 * @event DOMContentLoaded
 */
document.addEventListener('DOMContentLoaded', () => {

  obtenerCarreras();

  // Formulario crear
  const formCar = document.getElementById('form-carrera');
  if (formCar) {
    formCar.addEventListener('submit', async function(e) {
      e.preventDefault();
      //se arman los campos con los nombres que espera el MS2 (CarreraDTO)
      const nuevaCarrera = {
        nombreCarrera:   document.getElementById('car-nombre').value,
        ubicacion:       document.getElementById('car-ubicacion').value,
        fechaEjecucion:  parseInt(document.getElementById('car-fecha').value),
        nivelDificultad: document.getElementById('car-dificultad').value,
        idCategoria:     parseInt(document.getElementById('car-categoria-id').value),
        paraQuien:       document.getElementById('car-para-quien').value
      };
      try {
        const respuesta = await fetch(`${API_CARRERAS}/crearcarrera`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(nuevaCarrera)
        });
        if (respuesta.ok) {
          const creada = await respuesta.json();
          listaCarreras.push(creada); //se agrega a la lista local
          mostrarMensajeCar('exitoso', 'Carrera creada correctamente');
          formCar.reset();
          await obtenerCarreras();
        } else {
          const errorTexto = await respuesta.text();
          mostrarMensajeCar('error', errorTexto || 'Error al crear la carrera');
        }
      } catch (error) {
        mostrarMensajeCar('error', 'No se pudo conectar con MS2 (puerto 9001)');
      }
    });
  }

  // Buscar por ID
  const btnBuscar = document.getElementById('btn-buscar-car');
  if (btnBuscar) {
    btnBuscar.addEventListener('click', async () => {
      const id = document.getElementById('buscar-car-id').value;
      if (!id) { mostrarMensajeCar('warning', 'Ingrese un ID'); return; }
      try {
        const respuesta = await fetch(`${API_CARRERAS}/consultarcarrera/${id}`);
        if (respuesta.ok) {
          const car = await respuesta.json();
          //se guarda en la lista local para que los botones de editar/eliminar funcionen
          if (!listaCarreras.find(c => c.id === car.id)) listaCarreras.push(car);
          renderizarTablaCarreras([car]);
        } else {
          mostrarMensajeCar('error', 'Carrera no encontrada');
          renderizarTablaCarreras([]);
        }
      } catch (error) {
        mostrarMensajeCar('error', 'No se pudo conectar con MS2 (puerto 9001)');
      }
    });
  }

  // Ver todas (muestra las que se han cargado en la sesion)
  const btnVerTodas = document.getElementById('btn-ver-todas-car');
  if (btnVerTodas) {
    btnVerTodas.addEventListener('click', () => {
      document.getElementById('buscar-car-id').value = '';
      obtenerCarreras();
    });
  }

  // Botones guardar modificación
  const btnUbicacion = document.getElementById('btn-guardar-ubicacion');
  if (btnUbicacion) btnUbicacion.addEventListener('click', modificarUbicacion);

  const btnFecha = document.getElementById('btn-guardar-fecha');
  if (btnFecha) btnFecha.addEventListener('click', modificarFecha);
});
