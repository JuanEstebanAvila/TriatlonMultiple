/**
 * @fileoverview Módulo principal del FrontEnd para la gestión de atletas de Triatlón UD.
 * Maneja la comunicación con el BackEnd mediante Fetch API, y controla la visualización
 * dinámica de tablas, tarjetas, carrusel de especialidades, formularios de registro,
 * modificacion, eliminacion y consulta de atletas.
 *
 * @author Julian David Muñoz Revelo - 20251020042
 * @author Juan Steban Avila Trujillo - 20251020054
 * @author Juan Sneyder Méndez Gil - 20251020010
 * @version 1.0
 */

/**
 * URL base del BackEnd Spring Boot para los endpoints de atletas.
 * @constant {string}
 */

const API_URL = "http://localhost:8080/api/atletas";

/**
 * Lista global de atletas cargada desde el servidor.
 * Se inicializa vacía y se llena con la respuesta del BackEnd.
 * Se usa en todas las funciones de visualización y filtrado.
 * @type {Array<Object>}
 */

let listaAtletas = [];

/**
 * Realiza una petición asíncrona al BackEnd para obtener todos los atletas registrados.
 * Guarda los datos en la variable global listaAtletas, refresca la interfaz
 * y actualiza el contador de atletas en el index.
 * En caso de fallo muestra un mensaje de error al usuario.
 *
 * @async
 * @function obtenerAtletasDelServidor
 * @returns {Promise<void>}
 */
async function obtenerAtletasDelServidor(){
  try{
    //peticion de web asincrona
    //pausa la ejecucion de la funcion hasta que el servidor responda
    const respuesta = await fetch("http://localhost:8080/api/atletas");
    //detectar si una peticion a la web fallo
    if (!respuesta.ok) throw new Error ("Error al obtener datos");
    //convierte los datos crudos que envio el servidor
    //toma el cuerpo de la respuesta HTTP (que viaja por la red como texto plano en formato JSON)
    listaAtletas = await respuesta.json();

    refrescarInterfaz();

    const countAtletas = document.getElementById('count-atletas');
    if (countAtletas) countAtletas.textContent = listaAtletas.length;
  }

  catch (error){
    mostrarMensaje('error', 'No se pudo conectar al servidor');
  }
}

/**
 * Objeto que mapea cada especialidad de triatlón a una clase CSS de Bootstrap
 * que define el color del borde de las tarjetas y el carrusel.
 * Se usa en CrearTarjetas y CrearCarrusel.
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
 * Se usa solo en CrearCarrusel para mostrar el ícono de cada especialidad.
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
 * La categoría se determina según la reglamentación oficial de triatlon que manejamos.
 *
 * @function calcularCategoria
 * @param {number|string} edad - Edad del atleta. Se convierte a entero internamente.
 * @returns {string} Nombre de la categoría correspondiente a la edad.
 *
 * @example
 * calcularCategoria(18); // returns 'Junior'
 * calcularCategoria(45); // returns 'Veteranos'
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
  if (edad >= 40)          return 'Veteranos';   // 40 en adelante, veteranos juntos
  return 'Sin categoria';
}

/**
 * Oculta los paneles de confirmación de eliminación y modificación en listado.html.
 * Los if evitan errores en páginas donde esos elementos no existen.
 * Comprueba si el elemento realmente existe en la página antes de actuar
 * para que no falle con las otras páginas.
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
 * Traduce el tipo de mensaje a la clase CSS de Bootstrap correspondiente.
 * La alerta desaparece automáticamente después de 3 segundos.
 *
 * @function mostrarMensaje
 * @param {string} tipo - Tipo de mensaje. Valores válidos: 'exitoso', 'warning', 'error', 'sucess'.
 * @param {string} texto - Texto que se mostrará dentro de la alerta.
 * @returns {void}
 *
 * @example
 * mostrarMensaje('exitoso', 'Atleta eliminado correctamente');
 * mostrarMensaje('error', 'No se pudo conectar al servidor');
 */
function mostrarMensaje(tipo, texto) {
  // mapeo de tipo a clase Bootstrap
  const clases = {
    'exitoso': 'alert-success',
    'warning': 'alert-warning',
    'error':   'alert-danger',
    'sucess':  'alert-success'
  };

  //Busca la clase para el tipo recibido. Si el tipo no existe en el objeto, usa 'alert-info' como valor por defecto gracias al operador ||.
  const claseAlerta = clases[tipo] || 'alert-info';
  //Crea un elemento <div> nuevo en memoria
  const alerta = document.createElement('div');
  //se coloca en la esquina superior derecha
  alerta.className = `alert ${claseAlerta} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
  alerta.style.zIndex = 9999;
  alerta.innerHTML = `
        ${texto}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
  //se inserta el <div> al final del <body>
  document.body.appendChild(alerta);

  // desaparece automáticamente en 3 segundos
  setTimeout(() => alerta.remove(), 3000);
}
/**
 * Variable global que almacena la especialidad actualmente seleccionada en el carrusel.
 * Se usa para marcar visualmente la tarjeta activa al refrescar el carrusel.
 * @type {string}
 */
let especialidadActiva = 'Todas';

/**
 * Crea y renderiza el carrusel horizontal de especialidades en listado.html.
 * Busca el contenedor del carrusel. Si no existe (estamos en una página diferente
 * a listado.html), sale inmediatamente con return para no generar errores.
 * Extrae las especialidades únicas de listaAtletas, calcula cuántos atletas
 * tiene cada una y genera las tarjetas del carrusel con su color e ícono correspondiente.
 *
 * @function CrearCarrusel
 * @returns {void}
 */
function CrearCarrusel(){
  const seguimiento = document.getElementById('carrusel-t');
  if (!seguimiento) return;

  //extrae solo las especialidades de cada atleta como array
  const especialidades = ['todas', ...new Set (listaAtletas.map(a => a.especialidad))];
  //se limpia el contenido anterior para evitar que se duplique al refrescar
  seguimiento.innerHTML = '';
  //filtra por especialidad
  especialidades.forEach(esp => {
    const cantidad = esp === 'todas'
      ? listaAtletas.length
      : listaAtletas.filter(a => a.especialidad === esp).length;
    //busca el color y icono de esa especialidad sino por defecto
    const colorBorde = coloresEspecialidad[esp]|| 'border-primary';
    const icono = iconosEspecialidad[esp]|| 'bi-star';
    const activo = especialidadActiva === esp ? 'border-3 shadow-sm' : '';
    //el html del carrusel
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
 * Filtra la tabla y las tarjetas de atletas según la especialidad seleccionada en el carrusel.
 * Actualiza la variable global especialidadActiva para que CrearCarrusel sepa
 * cual marcar como activa al darle click.
 * filter crea un nuevo array sin modificar el original.
 *
 * @function filtrarPorEspecialidad
 * @param {string} esp - Nombre de la especialidad seleccionada. Si es 'todas' muestra todos los atletas.
 * @returns {void}
 */
function filtrarPorEspecialidad(esp){
  especialidadActiva = esp;
  //filtro (filter crea un nuevo array)
  const lista = esp === 'todas'
    ? listaAtletas
    : listaAtletas.filter(a => a.especialidad === esp);
  //se crean de nuevo tabla y tarjetas con la lista filtrada, y recarga el carrusel para actualizar
  //cual esta marcado como activo
  CrearTabla(lista);
  CrearTarjetas(lista);
  CrearCarrusel(); // refresca el carrusel para que se marque
}

/**
 * Recarga toda la interfaz visual con los datos actuales de listaAtletas.
 * Evita bloqueos o fallos usando if para verificar que los elementos existen
 * antes de llamar cada función. Esto evita errores en páginas como consulta.html
 * o registro.html donde la tabla y las tarjetas no existen.
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
 * Cada vez que se agrega o borra a alguien, se llama esta funcion para que actualice la vista.
 * document.getElementById va al HTML y agarra especificamente el contenedor de la tabla.
 * Toma la lista de atletas guardada y la convierte en etiquetas tr y td.
 *
 * @function CrearTabla
 * @param {Array<Object>} listaAtletas - Array de objetos atleta a renderizar en la tabla.
 * @returns {void}
 */
function CrearTabla(listaAtletas){
  // se busca el cuerpo de la tabla por su ID
  const cuerpoTabla = document.getElementById('tabla-atletas-body');

  //se limpia o borra el contenido de la tabla ya creado para evitar la duplicacion de la informacion
  cuerpoTabla.innerHTML="";

  listaAtletas.forEach(atleta => {
    //se crea una fila(tr) en memoria
    //cada atleta crea un elemento <tr> en memoria.
    const fila = document.createElement('tr');

    //se llena la fila con celdas (td)
    //lena la fila con los datos del atleta
    //toma una fila existente en una tabla HTML (<tr>) y llena su interior automáticamente con celdas <td>
    fila.innerHTML=`
        <td> <img src="img/atleta-default.png" width="40" class="rounded-circle"></td>
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
    //se agrega la fila al cuerpo
    //toma la fila que acabamos de armar con sus datos y botones, y la "pega" físicamente
    //dentro del HTML para que el usuario la vea
    cuerpoTabla.appendChild(fila);
  });
}

/**
 * Genera y renderiza las tarjetas visuales de atletas en listado.html.
 * Cada tarjeta muestra la foto, nombre, ID, categoría y especialidad del atleta,
 * con el color de borde correspondiente a su especialidad según coloresEspecialidad.
 *
 * @function CrearTarjetas
 * @param {Array<Object>} listaAtletas - Array de objetos atleta a renderizar como tarjetas.
 * @returns {void}
 */
function CrearTarjetas(listaAtletas){
  const contenedor = document.getElementById('vista-tarjetas');
  //limpia el contenido previo
  contenedor.innerHTML ="";

  listaAtletas.forEach(atleta=>{
    //busca color especialidad
    const colorClase = coloresEspecialidad[atleta.especialidad] || 'border-primary';
    // se agrega la tarjeta al contenedor
    contenedor.innerHTML +=`
        <div class="col-3">
            <div class="card h-100 card-atleta shadow-sm border-top ${colorClase} border-4">
                <img src="img/atleta-placeholder.jpg" class="card-img-top foto-atleta" alt="${atleta.nombre}">
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
 * Busca el atleta por ID en listaAtletas, llena el panel con su nombre e identificación,
 * lo hace visible y hace scroll suave hasta él para que el usuario lo vea.
 * innerText cambia solo el texto dentro de una etiqueta evitando que se inyecte HTML.
 *
 * @function prepararEliminacion
 * @param {number} idRecibido - ID del atleta que se desea eliminar.
 * @returns {void}
 */
function prepararEliminacion(idRecibido){
  //se busca el atleta, guarda la informacion en atletaencontrado
  //Recorre el arreglo devuelve el objeto completo del atleta que coincida con el id
  const atletaEncontrado = listaAtletas.find(a=> a.id === idRecibido);

  if (atletaEncontrado){
    // se llenan los textos de confirmacion que se muestran
    //innerText Cambia solo el texto dentro de una etiqueta evitando que se inyecte HTML
    document.getElementById('eliminar-nombre-texto').innerText = atletaEncontrado.nombre;
    document.getElementById('eliminar-id-texto').innerText = atletaEncontrado.identificacion;

    //se muestra el panel de eliminacion, se quita el d-none. se ocultan lo otro
    document.getElementById('contenedor-eliminar').classList.remove('d-none');
    //se le agrega el d-none a pesar que ya tenga uno, el otro es que al refrescar no se vea. este se recarga la pagina y se quita
    document.getElementById('contenedor-actualizar').classList.add('d-none');

    //botones de realizar accion de borrado
    const btnFinal = document.getElementById('btn-confirmar-borrado');
    btnFinal.onclick =() => ejecutarEliminacionReal(idRecibido);

    //funcion que se encontro para una mejora visual
    //Scroll suave hacia el panel para que el usuario lo vea
    //se encarga de que el navegador haga "scroll" (desplace la pantalla) automaticamente hasta el elemento que acabas de mostrar.
    document.getElementById('contenedor-eliminar').scrollIntoView({behavior: 'smooth'});
  }
}

/**
 * Ejecuta la eliminación definitiva de un atleta de listaAtletas.
 * Crea un nuevo array con todos los atletas excepto el que tiene ese ID
 * y lo reasigna a listaAtletas reemplazando el array anterior.
 * Luego oculta los paneles, refresca la interfaz y muestra un mensaje de éxito.
 *
 * @function ejecutarEliminacionReal
 * @param {number} idABorrar - ID del atleta que se eliminará definitivamente.
 * @returns {void}
 */
function ejecutarEliminacionReal(idABorrar){
  //se filtra para quedarse con todo menos lo que sea el mismo id
  listaAtletas = listaAtletas.filter(a => a.id !== idABorrar);

  //se limpia la interfaz
  ocultarSecciones(); //se esconde el de alerta
  refrescarInterfaz();//vuelve a dibujar la tabla sin el atleta
  mostrarMensaje('exitoso','atleta eliminado');
}

/**
 * Prepara y muestra el formulario de modificación con los datos actuales del atleta.
 * Busca el atleta por ID, precarga sus datos en los campos del formulario,
 * muestra el panel de actualización y oculta el de eliminación.
 * Hace scroll suave hasta el formulario para que el usuario lo vea.
 *
 * @function prepararModificacion
 * @param {number} idRecibido - ID del atleta cuyos datos se van a modificar.
 * @returns {void}
 */
function prepararModificacion(idRecibido){
  //se busca el atleta, guarda la informacion en atletaencontrado
  //Recorre el arreglo devuelve el objeto completo del atleta que coincida con el id
  const atleta = listaAtletas.find(a => a.id === idRecibido);

  if (atleta){
    //se carga los datos actuales en los inputs del formulario de actualización
    document.getElementById('modificacion-id-original').value = atleta.id;
    document.getElementById('modificacion-nombre').value = atleta.nombre;
    document.getElementById('modificacion-identificacion').value = atleta.identificacion;
    document.getElementById('modificacion-categoria').value = atleta.categoria;
    document.getElementById('modificacion-especialidad').value = atleta.especialidad;

    //se muestra el formulario de actualizacion y se oculta el de eliminar
    document.getElementById('contenedor-actualizar').classList.remove('d-none');
    document.getElementById('contenedor-eliminar').classList.add('d-none');
    document.getElementById('contenedor-actualizar').scrollIntoView({behavior: 'smooth'});

  }
}

/**
 * Renderiza la tabla de resultados en consulta.html con la lista de atletas recibida.
 * Muestra u oculta el mensaje de "no se encontraron resultados" dependiendo
 * si la lista está vacía. Se usa en consulta.html y es llamada por aplicarFiltros().
 *
 * @function consultarAtletas
 * @param {Array<Object>} listaAtletas - Array de atletas a mostrar en la tabla de consulta.
 * @returns {void}
 */
function consultarAtletas(listaAtletas) {
  const cuerpo = document.getElementById('tabla-consulta-body');
  if (!cuerpo) return;
  cuerpo.innerHTML = '';
  //se muestra o oculta el mensaje de "no se encontraron resultados" dependiendo si la lista esta vacia.
  const sinResultados = document.getElementById('mensaje-sin-resultados');

  if (listaAtletas.length === 0) {
    sinResultados.classList.remove('d-none');
  } else {
    sinResultados.classList.add('d-none');
    listaAtletas.forEach(atleta => {
      const fila = document.createElement('tr');
      fila.innerHTML = `
                <td>${atleta.identificacion}</td>
                <td>${atleta.nombre}</td>
                <td>${atleta.edad || '—'}</td>
                <td>${atleta.genero === 'M' ? 'Masculino' : atleta.genero === 'F' ? 'Femenino' : '—'}</td>
                <td><span class="badge bg-info text-dark">${atleta.categoria}</span></td>
                <td>${atleta.especialidad}</td>
                <td>${atleta.esCross ? '<span class="badge bg-success">Sí</span>' : '<span class="badge bg-secondary">No</span>'}</td>
            `;
      cuerpo.appendChild(fila);
    });
  }
}

/**
 * Lee los valores de todos los filtros de consulta.html y filtra listaAtletas
 * según los criterios seleccionados. Crea una copia del array original que
 * se va reduciendo con cada filtro activo sin modificar el original.
 * includes busca si la identificación contiene el texto escrito (búsqueda parcial).
 * Convierte true/false a texto para compararlo con el valor del select.
 * Al final llama consultarAtletas con la lista filtrada.
 *
 * @function aplicarFiltros
 * @returns {void}
 */
function aplicarFiltros() {
  //.trim() elimina espacios al inicio y al final
  const buscarId    = document.getElementById('buscar-id')?.value.trim().toLowerCase();
  const genero      = document.getElementById('filtro-genero')?.value;
  const categoria   = document.getElementById('filtro-categoria')?.value.toLowerCase();
  const especialidad = document.getElementById('filtro-especialidad')?.value;
  const cross = document.getElementById('filtro-cross')?.value;
  //crea una copia del array original
  //se reducira con cada filtro
  let lista = [...listaAtletas];
  //includes busca si la identificación contiene el texto escrito
  if (buscarId)     lista = lista.filter(a => a.identificacion.toLowerCase().includes(buscarId));
  if (genero)       lista = lista.filter(a => a.genero === genero);
  if (categoria)    lista = lista.filter(a => a.categoria.toLowerCase() === categoria);
  if (especialidad) lista = lista.filter(a => a.especialidad === especialidad);
  //convierte true/false a texto 'true'/'false' para compararlo con el valor del select que tambien es texto.
  if (cross !== '') lista = lista.filter(a => String(a.esCross) === cross);

  consultarAtletas(lista);
}

/**
 * Listener del formulario de registro de atletas.
 * Captura el evento submit, construye el objeto nuevoAtleta con los valores
 * de los campos del formulario, calcula la categoría automáticamente según la edad,
 * lee la especialidad del Swiper mediante el input oculto, y agrega el atleta
 * a listaAtletas. El typeof verifica que refrescarInterfaz existe y es una función
 * antes de llamarla, evitando el error 'refrescarInterfaz is not a function'.
 * id se genera con Date.now() para garantizar que siempre sea único.
 */
// para el formulario de registro (index.html o registro.html)
const formRegistro = document.getElementById('form-registro');

if (formRegistro){
  formRegistro.addEventListener('submit', function(e){
    e.preventDefault();

    //se crea el nuevo objeto de atleta capturando los valores
    const nuevoAtleta ={
      //se crea un id respecto de tiempo siempre seran distintos
      //es una forma que vi por un video
      id:             Date.now(),
      nombre:         document.getElementById('nombre').value,
      identificacion: document.getElementById('identificacion').value,
      edad:           document.getElementById('edad').value,
      genero:         document.getElementById('genero').value,
      categoria:      calcularCategoria(document.getElementById('edad').value), // calculo de categoria
      especialidad:   document.getElementById('input-especialidad').value,      // lee el swiper o slides
      esCross:        document.getElementById('esCross').checked
    };
    //.push añade uno o más elementos al final del la lista de atletas
    listaAtletas.push(nuevoAtleta);

    formRegistro.reset();
    mostrarMensaje('sucess', 'se guardo atleta nuevo');
    // El typeof verifica que si existe y es una funcion antes de llamarla, evitando el error refrescarInterfaz is not a function
    if (typeof refrescarInterfaz === "function"){
      refrescarInterfaz();
    }
  });
}

/**
 * Evento principal que se ejecuta cuando el navegador termina de cargar y construir
 * todo el HTML. Sin esto, getElementById devolvería null porque los elementos
 * aún no existen. Inicializa todos los listeners de botones y elementos interactivos
 * de las páginas listado.html, consulta.html y registro.html.
 *
 * espera a que todo el HTML se haya dibujado y los archivos CSS se hayan cargado
 * antes de intentar llenar la tabla
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
   * Sirve para que la página decida qué dibujar en la pantalla
   * dependiendo el estado de la variable llamada vista.
   * Comprueba si el valor de esa variable es exactamente el texto "tabla".
   *
   * @function cambiarVista
   * @param {string} vista - Vista a mostrar. Valores válidos: 'tabla' o cualquier otro valor para tarjetas.
   * @returns {void}
   */
  function cambiarVista(vista){
    if (vista === 'tabla'){
      //mostrar tabla, se ocultan las tarjetas
      vistaTabla.classList.remove('d-none');
      vistaTarjetas.classList.add('d-none');
      //se muestra activado boton de tabla desactivado boton tarjetas
      btnTabla.classList.add('active');
      btnTarjetas.classList.remove('active');
    }else{
      //contrario
      vistaTabla.classList.add('d-none');
      vistaTarjetas.classList.remove('d-none');

      btnTabla.classList.remove('active');
      btnTarjetas.classList.add('active');
    }
  }
  if (btnTabla) btnTabla.addEventListener('click', () => cambiarVista('tabla'));
  if (btnTarjetas) btnTarjetas.addEventListener('click', () => cambiarVista('tarjetas'));
//se usa para el calculo de la categoria segun edad
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

  if (btnBuscarId) {
    btnBuscarId.addEventListener('click', aplicarFiltros);
  }
  if (btnLimpiar) {
    btnLimpiar.addEventListener('click', () => {
      document.getElementById('buscar-id').value = '';
      document.getElementById('filtro-genero').value = '';
      document.getElementById('filtro-categoria').value = '';
      document.getElementById('filtro-especialidad').value = '';
      consultarAtletas(listaAtletas);
    });
  }
// Los selects filtran en tiempo real al cambiar
//change es el evento que dispara un <select>
  if (filtroGenero)       filtroGenero.addEventListener('change', aplicarFiltros);
  if (filtroCategoria)    filtroCategoria.addEventListener('change', aplicarFiltros);
  if (filtroEspecialidad) filtroEspecialidad.addEventListener('change', aplicarFiltros);

// Cargar todos al entrar a la página
  if (document.getElementById('tabla-consulta-body')) {
    consultarAtletas(listaAtletas);
  }

  const formActualizar = document.getElementById('form-actualizar');
  if (formActualizar) {
    formActualizar.addEventListener('submit', function(e) {
      e.preventDefault();
      const idABuscar = parseInt(document.getElementById('modificacion-id-original').value);
      const indice = listaAtletas.findIndex(a => a.id === idABuscar);
      if (indice !== -1) {
        listaAtletas[indice].nombre         = document.getElementById('modificacion-nombre').value;
        listaAtletas[indice].identificacion = document.getElementById('modificacion-identificacion').value;
        listaAtletas[indice].categoria      = document.getElementById('modificacion-categoria').value;
        listaAtletas[indice].especialidad   = document.getElementById('modificacion-especialidad').value;
        ocultarSecciones();
        refrescarInterfaz();
        mostrarMensaje('warning', 'Atleta actualizado correctamente');
      }
    });
  }

  // selector de especialidad con flechas
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

      // dots
      dotsEsp.innerHTML = '';
      especialidades.forEach((_, i) => {
        const dot = document.createElement('div');
        dot.style.cssText = `width:8px;height:8px;border-radius:50%;background:${i === actualEsp ? '#8c1919' : '#ccc'};transition:all 0.2s;`;
        dotsEsp.appendChild(dot);
      });

      btnEspPrev.disabled = actualEsp === 0;
      btnEspNext.disabled = actualEsp === especialidades.length - 1;

      // boton elegir
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
