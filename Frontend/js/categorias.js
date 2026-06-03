/**
 * Módulo de gestión de categorías de triatlón.
 * Maneja la comunicación con el microservicio MS3 (Categoria) en puerto 9002
 * mediante Fetch API para operaciones CRUD sobre categorías.
 */

/**
 * URL base del microservicio MS3 (Categoria).
 * @constant {string}
 */
const API_CATEGORIAS = "http://localhost:9002/api/categoria";

/**
 * Lista global de categorías cargada desde MS3.
 * @type {Array<Object>}
 */
let listaCategorias = [];

/**
 * Oculta los paneles de eliminar y modificar categoría.
 * @function ocultarPanelesCat
 * @returns {void}
 */
function ocultarPanelesCat() {
  const panelEliminar  = document.getElementById('contenedor-eliminar-cat');
  const panelModificar = document.getElementById('contenedor-modificar-cat');
  if (panelEliminar)  panelEliminar.classList.add('d-none');
  if (panelModificar) panelModificar.classList.add('d-none');
}

/**
 * Muestra una alerta flotante en la esquina superior derecha.
 *
 * @function mostrarMensajeCat
 * @param {string} tipo - 'exitoso', 'warning' o 'error'
 * @param {string} texto - Texto a mostrar en la alerta
 * @returns {void}
 */
function mostrarMensajeCat(tipo, texto) {
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
 * Obtiene todas las categorías del servidor MS3 y refresca la tabla.
 * El MS3 si tiene un endpoint "consultartodas".
 *
 * @async
 * @function obtenerCategorias
 * @returns {Promise<void>}
 */
async function obtenerCategorias() {
  try {
    const respuesta = await fetch(`${API_CATEGORIAS}/consultartodas`);
    if (!respuesta.ok) throw new Error("Error al obtener categorías");
    listaCategorias = await respuesta.json();
    renderizarTabla(listaCategorias);
    const contador = document.getElementById('count-categorias');
    if (contador) contador.textContent = listaCategorias.length;
  } catch (error) {
    mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
  }
}

/**
 * Renderiza la tabla HTML con la lista de categorías recibida.
 *
 * @function renderizarTabla
 * @param {Array<Object>} lista - Array de categorías a renderizar
 * @returns {void}
 */
function renderizarTabla(lista) {
  const cuerpo = document.getElementById('tabla-categorias-body');
  const sinResultados = document.getElementById('mensaje-sin-categorias');
  if (!cuerpo) return;
  cuerpo.innerHTML = '';

  if (lista.length === 0) {
    if (sinResultados) sinResultados.classList.remove('d-none');
    return;
  }
  if (sinResultados) sinResultados.classList.add('d-none');

  lista.forEach(cat => {
    const fila = document.createElement('tr');
    fila.innerHTML = `
            <td><span class="badge bg-secondary">${cat.id}</span></td>
            <td class="fw-bold">${cat.nombreCategoria}</td>
            <td><span class="badge bg-info text-dark">${cat.tipo}</span></td>
            <td class="text-muted small">${cat.descripcion || '—'}</td>
            <td class="text-muted small">${cat.recomendacion || '—'}</td>
            <td class="text-end">
                <button class="btn btn-sm btn-warning me-1"
                        onclick="prepararModificacionCat(${cat.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger"
                        onclick="prepararEliminacionCat(${cat.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
    cuerpo.appendChild(fila);
  });
}

/**
 * Muestra el panel de confirmación de eliminación con los datos de la categoría.
 *
 * @function prepararEliminacionCat
 * @param {number} id - ID de la categoría a eliminar
 * @returns {void}
 */
function prepararEliminacionCat(id) {
  const cat = listaCategorias.find(c => c.id === id);
  if (!cat) return;

  document.getElementById('eliminar-cat-nombre').innerText = cat.nombreCategoria;
  document.getElementById('eliminar-cat-tipo').innerText   = cat.tipo;
  document.getElementById('contenedor-eliminar-cat').classList.remove('d-none');
  document.getElementById('contenedor-modificar-cat').classList.add('d-none');

  document.getElementById('btn-confirmar-borrado-cat').onclick = () => eliminarCategoria(id);
  document.getElementById('contenedor-eliminar-cat').scrollIntoView({ behavior: 'smooth' });
}

/**
 * Ejecuta la eliminación de una categoría llamando al endpoint DELETE de MS3.
 *
 * @async
 * @function eliminarCategoria
 * @param {number} id - ID de la categoría a eliminar
 * @returns {Promise<void>}
 */
async function eliminarCategoria(id) {
  try {
    const respuesta = await fetch(`${API_CATEGORIAS}/eliminar/${id}`, {
      method: 'DELETE'
    });
    if (respuesta.ok) {
      mostrarMensajeCat('exitoso', 'Categoría eliminada correctamente');
      ocultarPanelesCat();
      await obtenerCategorias();
    } else if (respuesta.status === 404) {
      mostrarMensajeCat('error', 'La categoría no existe');
    } else {
      mostrarMensajeCat('error', 'Error al eliminar la categoría');
    }
  } catch (error) {
    mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
  }
}

/**
 * Muestra el panel de modificación precargado con los datos actuales.
 *
 * @function prepararModificacionCat
 * @param {number} id - ID de la categoría a modificar
 * @returns {void}
 */
function prepararModificacionCat(id) {
  const cat = listaCategorias.find(c => c.id === id);
  if (!cat) return;

  document.getElementById('mod-cat-id').value            = cat.id;
  document.getElementById('mod-cat-descripcion').value   = cat.descripcion  || '';
  document.getElementById('mod-cat-recomendacion').value = cat.recomendacion || '';

  document.getElementById('contenedor-modificar-cat').classList.remove('d-none');
  document.getElementById('contenedor-eliminar-cat').classList.add('d-none');
  document.getElementById('contenedor-modificar-cat').scrollIntoView({ behavior: 'smooth' });
}

/**
 * Envía el PATCH de descripción al endpoint correspondiente de MS3.
 * El MS3 espera un cuerpo JSON con la clave "descripcion".
 *
 * @async
 * @function modificarDescripcion
 * @returns {Promise<void>}
 */
async function modificarDescripcion() {
  const id    = document.getElementById('mod-cat-id').value;
  const valor = document.getElementById('mod-cat-descripcion').value.trim();
  if (!valor) { mostrarMensajeCat('warning', 'Ingrese una descripción'); return; }

  try {
    const respuesta = await fetch(`${API_CATEGORIAS}/modificardescripcion/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ descripcion: valor })
    });
    if (respuesta.ok) {
      mostrarMensajeCat('exitoso', 'Descripción actualizada');
      ocultarPanelesCat();
      await obtenerCategorias();
    } else {
      mostrarMensajeCat('error', 'Error al actualizar descripción');
    }
  } catch (error) {
    mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
  }
}

/**
 * Envía el PATCH de recomendación al endpoint correspondiente de MS3.
 * El MS3 espera un cuerpo JSON con la clave "recomendacion".
 *
 * @async
 * @function modificarRecomendacion
 * @returns {Promise<void>}
 */
async function modificarRecomendacion() {
  const id    = document.getElementById('mod-cat-id').value;
  const valor = document.getElementById('mod-cat-recomendacion').value.trim();
  if (!valor) { mostrarMensajeCat('warning', 'Ingrese una recomendación'); return; }

  try {
    const respuesta = await fetch(`${API_CATEGORIAS}/modificarrecomendacion/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ recomendacion: valor })
    });
    if (respuesta.ok) {
      mostrarMensajeCat('exitoso', 'Recomendación actualizada');
      ocultarPanelesCat();
      await obtenerCategorias();
    } else {
      mostrarMensajeCat('error', 'Error al actualizar recomendación');
    }
  } catch (error) {
    mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
  }
}

/**
 * Evento principal — inicializa listeners cuando el DOM está listo.
 * @event DOMContentLoaded
 */
document.addEventListener('DOMContentLoaded', () => {

  obtenerCategorias();

  // Formulario crear
  const formCat = document.getElementById('form-categoria');
  if (formCat) {
    formCat.addEventListener('submit', async function(e) {
      e.preventDefault();
      //se arman los campos con los nombres que espera el MS3 (CategoriaDTO)
      const nuevaCategoria = {
        nombreCategoria: document.getElementById('cat-nombre').value,
        tipo:            document.getElementById('cat-tipo').value,
        descripcion:     document.getElementById('cat-descripcion').value,
        recomendacion:   document.getElementById('cat-recomendacion').value
      };
      try {
        const respuesta = await fetch(`${API_CATEGORIAS}/crearcategoria`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(nuevaCategoria)
        });
        if (respuesta.ok) {
          mostrarMensajeCat('exitoso', 'Categoría creada correctamente');
          formCat.reset();
          await obtenerCategorias();
        } else {
          const errorTexto = await respuesta.text();
          mostrarMensajeCat('error', errorTexto || 'Error al crear la categoría');
        }
      } catch (error) {
        mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
      }
    });
  }

  // Buscar por ID
  const btnBuscar = document.getElementById('btn-buscar-cat');
  if (btnBuscar) {
    btnBuscar.addEventListener('click', async () => {
      const id = document.getElementById('buscar-cat-id').value;
      if (!id) { mostrarMensajeCat('warning', 'Ingrese un ID'); return; }
      try {
        const respuesta = await fetch(`${API_CATEGORIAS}/consultarcategoria/${id}`);
        if (respuesta.ok) {
          const cat = await respuesta.json();
          renderizarTabla([cat]);
        } else {
          mostrarMensajeCat('error', 'Categoría no encontrada');
          renderizarTabla([]);
        }
      } catch (error) {
        mostrarMensajeCat('error', 'No se pudo conectar con MS3 (puerto 9002)');
      }
    });
  }

  // Ver todas
  const btnVerTodas = document.getElementById('btn-ver-todas');
  if (btnVerTodas) {
    btnVerTodas.addEventListener('click', () => {
      document.getElementById('buscar-cat-id').value = '';
      obtenerCategorias();
    });
  }

  // Botones guardar modificación
  const btnGuardarDesc = document.getElementById('btn-guardar-descripcion');
  if (btnGuardarDesc) btnGuardarDesc.addEventListener('click', modificarDescripcion);

  const btnGuardarRec = document.getElementById('btn-guardar-recomendacion');
  if (btnGuardarRec) btnGuardarRec.addEventListener('click', modificarRecomendacion);
});
