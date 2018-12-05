var datos;
var datos2;
var sumaDisponible = 0;
var sumaNoDisponible = 0;
var sumaDisponible2 = 0;
var sumaNoDisponible2 = 0;
var cuerpo = "";
var cuerpo2 = "";
var cont = 0;
var tabladetalle1;
var tabladetalle2;

function init() {

    $('#date_inicio1').datepicker({
        format: 'dd/mm/yyyy',
    });
    $('#date_fin1').datepicker({
        format: 'dd/mm/yyyy',
    });
}


function buscar_reporte() {
    var Inicio = $("#inicio1").val();
    var Fin = $("#fin1").val();
    if (Inicio == '' || Fin == '') {
        notificar_warning("Ingrese Parametros Necesarios.")
    } else {
        mostrar_Tabla_detalles1(Inicio, Fin);
       mostrar_Tabla_detalles2(Inicio, Fin);
    }
}

function mostrar_Tabla_detalles1(Inicio, Fin) {
    if (tabladetalle1 == null) {
        tabladetalle1 = $('#tabla_Detalles1').dataTable({
            "aProcessing": true,
            "aServerSide": true,
            "processing": true,
            "paging": false, // Paginacion en tabla
            "ordering": true, // Ordenamiento en columna de tabla
            "info": true, // Informacion de cabecera tabla
            "responsive": true, // Accion de responsive
            dom: 'lBfrtip',
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
            "order": [[0, "asc"]],
            "bDestroy": true,
            "columnDefs": [
                {
                    "className": "text-center",
                    "targets": [0, 1, 2, 3, 4]
            }
            , {
                    "className": "text-left",
                    "targets": []
            }
         , ],
            buttons: [
                {
                    extend: 'copy',
                    className: 'btn-info',
                    title: "Reporte"
            }

            , {
                    extend: 'excel',
                    className: 'btn-info',
                    title: "Reporte"
            }
            , {
                    extend: 'pdfHtml5',
                    className: 'btn-info sombra3',
                    title: "Reporte",
                    orientation: 'landscape',
                    pageSize: 'LEGAL'
            }
            , {
                    extend: 'print',
                    className: 'btn-info',
                    title: "Reporte"
            }
            ],
            "ajax": { //Solicitud Ajax Servidor
                url: '../../controlador/Gestion/CGestion.php?op=ListarReportes1',
                type: "POST",
                dataType: "JSON",
                data: {
                    Inicio: Inicio,
                    Fin: Fin
                },
                error: function (e) {
                    console.log(e.responseText);
                }
            }, // cambiar el lenguaje de datatable
            oLanguage: español,
        }).DataTable();
        //Aplicar ordenamiento y autonumeracion , index
        tabladetalle1.on('order.dt search.dt', function () {
            tabladetalle1.column(0, {
                search: 'applied',
                order: 'applied'
            }).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();
    } else {
        tabladetalle1.destroy();
        tabladetalle1 = $('#tabla_Detalles1').dataTable({
            "aProcessing": true,
            "aServerSide": true,
            "processing": true,
            "paging": false, // Paginacion en tabla
            "ordering": true, // Ordenamiento en columna de tabla
            "info": true, // Informacion de cabecera tabla
            "responsive": true, // Accion de responsive
            dom: 'lBfrtip',
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
            "order": [[0, "asc"]],
            "bDestroy": true,
            "columnDefs": [
                {
                    "className": "text-center",
                    "targets": [0, 1, 2, 3, 4]
            }
            , {
                    "className": "text-left",
                    "targets": []
            }
         , ],
            buttons: [
                {
                    extend: 'copy',
                    className: 'btn-info',
                    title: "Reporte"
            }

            , {
                    extend: 'excel',
                    className: 'btn-info',
                    title: "Reporte"
            }
            , {
                    extend: 'pdfHtml5',
                    className: 'btn-info sombra3',
                    title: "Reporte",
                    orientation: 'landscape',
                    pageSize: 'LEGAL'
            }
            , {
                    extend: 'print',
                    className: 'btn-info',
                    title: "Reporte"
            }
            ],
            "ajax": { //Solicitud Ajax Servidor
                url: '../../controlador/Gestion/CGestion.php?op=ListarReportes1',
                type: "POST",
                dataType: "JSON",
                data: {
                    Inicio: Inicio,
                    Fin: Fin
                },
                error: function (e) {
                    console.log(e.responseText);
                }
            }, // cambiar el lenguaje de datatable
            oLanguage: español,
        }).DataTable();
        //Aplicar ordenamiento y autonumeracion , index
        tabladetalle1.on('order.dt search.dt', function () {
            tabladetalle1.column(0, {
                search: 'applied',
                order: 'applied'
            }).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();
    }
}

function mostrar_Tabla_detalles2(Inicio, Fin) {
    if (tabladetalle2 == null) {
        tabladetalle2 = $('#tabla_Detalles2').dataTable({
            "aProcessing": true,
            "aServerSide": true,
            "processing": true,
            "paging": false, // Paginacion en tabla
            "ordering": true, // Ordenamiento en columna de tabla
            "info": true, // Informacion de cabecera tabla
            "responsive": true, // Accion de responsive
            dom: 'lBfrtip',
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
            "order": [[0, "asc"]],
            "bDestroy": true,
            "columnDefs": [
                {
                    "className": "text-center",
                    "targets": [0, 1, 2, 3, 4]
            }
            , {
                    "className": "text-left",
                    "targets": []
            }
         , ],
            buttons: [
                {
                    extend: 'copy',
                    className: 'btn-info',
                    title: "Reporte"
            }

            , {
                    extend: 'excel',
                    className: 'btn-info',
                    title: "Reporte"
            }
            , {
                    extend: 'pdfHtml5',
                    className: 'btn-info sombra3',
                    title: "Reporte",
                    orientation: 'landscape',
                    pageSize: 'LEGAL'
            }
            , {
                    extend: 'print',
                    className: 'btn-info',
                    title: "Reporte"
            }
            ],
            "ajax": { //Solicitud Ajax Servidor
                url: '../../controlador/Gestion/CGestion.php?op=ListarReportes2',
                type: "POST",
                dataType: "JSON",
                data: {
                    Inicio: Inicio,
                    Fin: Fin
                },
                error: function (e) {
                    console.log(e.responseText);
                }
            }, // cambiar el lenguaje de datatable
            oLanguage: español,
        }).DataTable();
        //Aplicar ordenamiento y autonumeracion , index
        tabladetalle2.on('order.dt search.dt', function () {
            tabladetalle2.column(0, {
                search: 'applied',
                order: 'applied'
            }).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();
    } else {
        tabladetalle2.destroy();
        tabladetalle2 = $('#tabla_Detalles2').dataTable({
            "aProcessing": true,
            "aServerSide": true,
            "processing": true,
            "paging": false, // Paginacion en tabla
            "ordering": true, // Ordenamiento en columna de tabla
            "info": true, // Informacion de cabecera tabla
            "responsive": true, // Accion de responsive
            dom: 'lBfrtip',
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
            "order": [[0, "asc"]],
            "bDestroy": true,
            "columnDefs": [
                {
                    "className": "text-center",
                    "targets": [0, 1, 2, 3, 4]
            }
            , {
                    "className": "text-left",
                    "targets": []
            }
         , ],
            buttons: [
                {
                    extend: 'copy',
                    className: 'btn-info',
                    title: "Reporte"
            }

            , {
                    extend: 'excel',
                    className: 'btn-info',
                    title: "Reporte"
            }
            , {
                    extend: 'pdfHtml5',
                    className: 'btn-info sombra3',
                    title: "Reporte",
                    orientation: 'landscape',
                    pageSize: 'LEGAL'
            }
            , {
                    extend: 'print',
                    className: 'btn-info',
                }
            ],
            "ajax": { //Solicitud Ajax Servidor
                url: '../../controlador/Gestion/CGestion.php?op=ListarReportes2',
                type: "POST",
                dataType: "JSON",
                data: {
                    Inicio: Inicio,
                    Fin: Fin
                },
                error: function (e) {
                    console.log(e.responseText);
                }
            }, // cambiar el lenguaje de datatable
            oLanguage: español,
        }).DataTable();
        //Aplicar ordenamiento y autonumeracion , index
        tabladetalle2.on('order.dt search.dt', function () {
            tabladetalle2.column(0, {
                search: 'applied',
                order: 'applied'
            }).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();
    }
}
init();
