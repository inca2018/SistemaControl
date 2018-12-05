<!-- Inicio de Cabecera-->
<?php require_once('../layaout/Header.php');?>
<!-- fin Cabecera-->
<!-- Inicio del Cuerpo y Nav -->
<?php require_once('../layaout/Nav.php');?>
<!-- Fin  del Cuerpo y Nav -->
<!-- Cuerpo del sistema de Menu -->
   <!-- Main section-->
   <section class="section-container">
      <!-- Page content-->
      	<div class="content-wrapper">

            <!-- START card-->
            <div class="card card-default">
               <div class="card-body">
							<div class="row ">
                            <div class="col-md-12 w-100 text-center ">
                                <h3>Panel de Reporte General:</h3>
                            </div>
							</div>
 							<div class="row justify-content-center m-3">
								 <div class="col-md-4 col-12">
                                        <div class="form-group row">
                                            <label for="inicio1" class="col-md-5 col-form-label"><i class="far fa-calendar-check fa-lg mr-2"></i>Fecha Inicio:<span class="red">*</span>:</label>
                                            <div class="col-md-7">
                                                <div class=" row">
																<div class="input-group date  col-md-12" id="date_inicio1"   >
																	<input class="form-control validarPanel" type="text" id="inicio1" name="inicio1"  autocomplete="off" data-message="- Fecha de Nacimiento">
																	<span class="input-group-append input-group-addon">
																		<span class="input-group-text "><i class="fa fa-calendar fa-lg"></i></span>
																	</span>
																</div>
															</div>
                                            </div>
                                        </div>
									</div>
                          <div class="col-md-4 col-12">
                                        <div class="form-group row">
                                            <label for="fin1" class="col-md-5 col-form-label"><i class="far fa-calendar-check fa-lg mr-2"></i>Fecha Fin:<span class="red">*</span>:</label>
                                            <div class="col-md-7">
                                                <div class=" row">
																<div class="input-group date  col-md-12" id="date_fin1"   >
																	<input class="form-control validarPanel" type="text" id="fin1" name="fin1"  autocomplete="off" data-message="- Fecha de Nacimiento">
																	<span class="input-group-append input-group-addon">
																		<span class="input-group-text "><i class="fa fa-calendar fa-lg"></i></span>
																	</span>
																</div>
															</div>
                                            </div>
                                        </div>
									</div>


                    </div>
                     <div class="row justify-content-center m-3">
                        <button type="button" class="btn btn-success col-md-6" onclick="buscar_reporte()">BUSCAR RESULTADOS</button>
                    </div>
                    <hr>

                      <div class="row">
                    <div class="col-md-6">
                       <h4 class="modal-title mt-3 mb-3 bb" id="myModalLabelLarge">Indice de Cumplimiento:</h4>

                        <div class="row">
                            <div class="col-md-12">
                                <table class="table my-4 w-100 table-hover table-sm dt-responsive" id="tabla_Detalles1" style="font-size:10px">
                                    <thead class="thead-light">
                                       <tr>
                                            <th  width="10%" data-priority="1">#</th>
                                            <th width="15%">Fecha</th>
                                            <th width="15%">Unidad de Analisis(UA)</th>
                                            <th width="15%">Nº Pedidos Entregados(NPEC)</th>
                                            <th width="15%">Total de Pedidos Solicitados(NTPS)</th>
                                            <th width="15%">% Cumplimiento de Entrega</th>
                                            <th width="15%">% Por Dia</th>
                                        </tr>

                                    </thead>
                                    <tbody id="body_detalles1">

                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                         <h4 class="modal-title mt-3 mb-3 bb" id="myModalLabelLarge">Indice de Eficacia: </h4>
                        <div class="row">
                            <div class="col-md-12">
                                <table class="table my-4 w-100 table-hover table-sm dt-responsive " id="tabla_Detalles2" style="font-size:10px">
                                    <thead class="thead-light">
													<tr>
                                            <th width="5%" data-priority="1">#</th>
                                            <th width="10%">Fecha</th>
                                            <th width="25%">Empleado</th>
                                            <th width="10%">Resultado Alcanzado(RA)</th>
                                            <th width="10%">Producción del Dia(PD)</th>
                                            <th width="10%">Producción Pendiente(PP)</th>
                                            <th width="10%">Total(PP+PD=RE)</th>
                                            <th width="10%">(RA/RE)</th>
                                            <th width="10%">% Por Dia</th>
                                        </tr>
                                    </thead>
                                    <tbody id="body_detalles2">

                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>
                </div>

               </div>
           </div>
         </div>
   </section>
    <!-- Fin Modal Agregar-->
<!-- Fin del Cuerpo del Sistema del Menu-->
<!-- Inicio del footer -->
<?php require_once('../layaout/Footer.php');?>
<!-- Fin del Footer -->


<script src="<?php echo $conexionConfig->rutaOP(); ?>vista/js/Reporte.js"></script>
