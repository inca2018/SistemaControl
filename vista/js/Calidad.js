var tablaCalidad;
var tablaGestionOvillado;
function init(){
	Listar_Calidad();

}

function Listar_Calidad() {

    tablaCalidad = $('#tablaCalidad').dataTable({
        "aProcessing": true,
        "aServerSide": true,
        "processing": true,
        "paging": true, // Paginacion en tabla
        "ordering": true, // Ordenamiento en columna de tabla
        "info": false, // Informacion de cabecera tabla
        "responsive": true, // Accion de responsive
        dom: 'lBfrtip',
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        "order": [[0, "asc"]],
        "bDestroy": true,
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [1, 2]
            }
            , {
                "className": "text-left",
                "targets": [0]
            }
         , ],
        buttons: [
            {
                extend: 'copy',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'excel',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'pdfHtml5',
                className: 'btn-info sombra3',
                title: "Sistema",
                orientation: 'landscape',
                pageSize: 'LEGAL'
            }
            , {
                extend: 'print',
                className: 'btn-info',
                title: "Sistema"
            }
            ],
        "ajax": { //Solicitud Ajax Servidor
            url: '../../controlador/Gestion/CCalidad.php?op=Listar_Ovillado',
            type: "POST",
            dataType: "JSON",
            error: function (e) {
                console.log(e.responseText);
            }
        },
        // cambiar el lenguaje de datatable
        oLanguage: español,
    }).DataTable();
    //Aplicar ordenamiento y autonumeracion , index
    tablaCalidad.on('order.dt search.dt', function () {
        tablaCalidad.column(0, {
            search: 'applied',
            order: 'applied'
        }).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();
}



function OrdenesOvilladoLista(idOrden){
	$("#ModalTrabajos").modal("show");
	Listar_Gestion_lista(idOrden);
}

function Listar_Gestion_lista(idOrden) {
	if(tablaGestionOvillado==null){
		 tablaGestionOvillado = $('#tablaTrabajos').dataTable({
        "aProcessing": true,
        "aServerSide": true,
        "processing": true,
        "paging": true, // Paginacion en tabla
        "ordering": true, // Ordenamiento en columna de tabla
        "info": false, // Informacion de cabecera tabla
        "responsive": true, // Accion de responsive
        dom: 'lBfrtip',
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        "order": [[0, "asc"]],
        "bDestroy": true,
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [1, 2]
            }
            , {
                "className": "text-left",
                "targets": [0]
            }
         , ],
        buttons: [
            {
                extend: 'copy',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'excel',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'pdfHtml5',
                className: 'btn-info sombra3',
                title: "Sistema",
                orientation: 'landscape',
                pageSize: 'LEGAL'
            }
            , {
                extend: 'print',
                className: 'btn-info',
                title: "Sistema"
            }
            ],
        "ajax": { //Solicitud Ajax Servidor
            url: '../../controlador/Gestion/COvillado.php?op=Listar_Gestion_trabajos',
            type: "POST",
            dataType: "JSON",
			   data:{idOrden:idOrden},
            error: function (e) {
                console.log(e.responseText);
            }
        },
        // cambiar el lenguaje de datatable
        oLanguage: español,
    }).DataTable();
    //Aplicar ordenamiento y autonumeracion , index
    tablaGestionOvillado.on('order.dt search.dt', function () {
        tablaGestionOvillado.column(0, {
            search: 'applied',
            order: 'applied'
        }).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

		}else{
	 tablaGestionOvillado.destroy();

    tablaGestionOvillado = $('#tablaTrabajos').dataTable({
        "aProcessing": true,
        "aServerSide": true,
        "processing": true,
        "paging": true, // Paginacion en tabla
        "ordering": true, // Ordenamiento en columna de tabla
        "info": false, // Informacion de cabecera tabla
        "responsive": true, // Accion de responsive
        dom: 'lBfrtip',
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        "order": [[0, "asc"]],
        "bDestroy": true,
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [1, 2]
            }
            , {
                "className": "text-left",
                "targets": [0]
            }
         , ],
        buttons: [
            {
                extend: 'copy',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'excel',
                className: 'btn-info',
                title: "Sistema"
            }
            , {
                extend: 'pdfHtml5',
                className: 'btn-info sombra3',
                title: "Sistema",
                orientation: 'landscape',
                pageSize: 'LEGAL'
            }
            , {
                extend: 'print',
                className: 'btn-info',
                title: "Sistema"
            }
            ],
        "ajax": { //Solicitud Ajax Servidor
            url: '../../controlador/Gestion/COvillado.php?op=Listar_Gestion_trabajos',
            type: "POST",
            dataType: "JSON",
			   data:{idOrden:idOrden},
            error: function (e) {
                console.log(e.responseText);
            }
        },
        // cambiar el lenguaje de datatable
        oLanguage: español,
    }).DataTable();
    //Aplicar ordenamiento y autonumeracion , index
    tablaGestionOvillado.on('order.dt search.dt', function () {
        tablaGestionOvillado.column(0, {
            search: 'applied',
            order: 'applied'
        }).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();
			}

}
init();
