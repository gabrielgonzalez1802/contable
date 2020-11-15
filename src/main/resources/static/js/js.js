// imprimirAbonoDetalle(2);

function addEvents(){
	
/************************************************** Configuraciones Iniciales **********************************************/
	
    $('[data-toggle="tooltip"]').tooltip();
    
	var clienteSeleccionado = $("#clienteSeleccionado").select2({
	    theme: 'bootstrap4',
	});
		
	
	if($("#tabla").length) {
		if( $.fn.DataTable.isDataTable('#tabla') == false){
			$('#tabla').DataTable({
			    "language": {
			        "sProcessing":    "Procesando...",
			        "sLengthMenu":    "Mostrar _MENU_ registros",
			        "sZeroRecords":   "No se encontraron resultados",
			        "sEmptyTable":    "Ningún dato disponible en esta tabla",
			        "sInfo":          "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
			        "sInfoEmpty":     "Mostrando registros del 0 al 0 de un total de 0 registros",
			        "sInfoFiltered":  "(filtrado de un total de _MAX_ registros)",
			        "sInfoPostFix":   "",
			        "sSearch":        "Buscar:",
			        "sUrl":           "",
			        "sInfoThousands":  ",",
			        "sLoadingRecords": "Cargando...",
			        "oPaginate": {
			            "sFirst":    "Primero",
			            "sLast":    "Último",
			            "sNext":    "Siguiente",
			            "sPrevious": "Anterior"
			        },
			        "oAria": {
			            "sSortDescending": ": Activar para ordenar la columna de manera descendente"
			        }
			    }
			});
		}
	}
	
	$("#changeEmpresa").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#modalCambioEmpresa").modal("show");
	});
	
	$("#btnCambioEmpresa").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idEmpresa = $("#selectCambioEmpresa").val(); 
		$("#selectCambioEmpresa").load("/empresas/change",
			{
				'idEmpresa': idEmpresa,
			},
		function(data){
			location.reload();
		});
		$("#modalCambioEmpresa").modal("hide");
	});

/*********************************************** Fin Configuraciones Iniciales **********************************************/	

/******************************************************* Clientes ***********************************************************/	
	//Buscar cliente
	$("#buscarPorDocumento").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		ocultarDetalleAmortizacion();
	     if(e.which == 13){
	    	 var tipoDocumento = "";
	    	 var carpeta = $("#carpetaId").val();
	    	 var item = $("#buscarPorDocumento").val();
		     if($("#cedula").is(':checked')){
		    	 tipoDocumento = "cedula";
		     }else if($("#otro").is(':checked')){
		    	 tipoDocumento = "otro";
		     }
		     var longitud = item.length;
		     if(longitud > 1){
			     $("#contenido").load("/clientes/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': tipoDocumento,
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							$("#cedula").attr('checked', 'checked');
							$("#otro").attr('checked', false);
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#btnDetalleCliente").click(function(e){
		$('#modalInfoCliente').modal('show');
	});
	
	$("input[name='tipoDocumentoBusqueda']").change(function(){
		 if($("#cedula").is(':checked')){
	    	 $('#buscarPorDocumento').attr('maxlength', 13);
	     }else if($("#otro").is(':checked')){
	    	 $('#buscarPorDocumento').attr('maxlength', 30);
	     }
		$('#buscarPorDocumento').val("");
	});
	
	$("#buscarPorNombre").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		ocultarDetalleAmortizacion();
	     if(e.which == 13){
	    	var item = $("#buscarPorNombre").val();
	    	var carpeta = $("#carpetaId").val();
	    	var longitud = item.length;
		     if(longitud > 1){
			     $("#contenido").load("/clientes/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': '0',
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}	
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
					}
					addEvents();
				});
		     }
	     }
	});
	
	//Validacion para cedula
	$('#buscarPorDocumento').on('keyup', function(e) { 
		if($("#cedula").is(':checked')){
			var valor = $('#buscarPorDocumento').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#buscarPorDocumento').val(valor.substring(0, valor.length - 1));
			}
		}	
	});
	 
	$('#buscarPorDocumento').on('keydown', function(e) { 
		if($("#cedula").is(':checked')){
			var valor = $('#buscarPorDocumento').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#buscarPorDocumento').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#buscarPorDocumento').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#buscarPorDocumento').val(valor+"-");
				 }
				}
			}
		}
	});
	
	$("#btnCarpeta").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		Swal.fire({
			  title: 'Ingrese la carpeta',
			  position: 'top',
			  input: 'text',
			  inputAttributes: {
			    autocapitalize: 'off'
			  },
			  showCancelButton: true,
			  confirmButtonText: 'Buscar',
			  cancelButtonText: 'Cancelar',
			  showLoaderOnConfirm: true,
			  preConfirm: (carpeta) => {
				  $.ajax({
			            url: "/carpetas/buscar/"+carpeta,
			            type: "GET",
//			            data: "",
//			            enctype: 'multipart/form-data',
			            processData: false,
			            contentType: false,
			            cache: false,
			            success: function (res) {
				            if(res=="No tienes carpetas creadas"){
								Swal.fire({
									title : 'No tienes carpetas creadas!',
//									text : 'No tienes carpetas creadas',
									position : 'top',
									icon : 'warning',
									confirmButtonText : 'Cool'
								})
								$("#contenido").load("/clientes/buscarCliente",function(data){
									console.log("Lista de clientes");
									ocultarDetalleAmortizacion();
									addEvents();
								});
							}else{
								Swal.fire({
									title : 'Muy bien!',
									text : 'Carpeta Seleccionada',
									position : 'top',
									icon : 'success',
									confirmButtonText : 'Cool'
								})
								e.preventDefault();
								$("#contenido").load("/clientes/buscarCliente",function(data){
									console.log("Lista de clientes");
									ocultarDetalleAmortizacion();
									addEvents();
								});
							}
			            },
			            error: function (err) {
			                console.error(err);
			                Swal.fire({
								title : 'Error!',
								text : 'No se pudo completar la operacion, intente mas tarde',
								position : 'top',
								icon : 'error',
								confirmButtonText : 'Cool'
							})
			            }
			        });
			  },
			  allowOutsideClick: () => !Swal.isLoading()
			}).then((result) => {
			  if (result.isConfirmed) {
			    //Correcto
			  }
			})
	});
	
	$("#btnExitCarpeta").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarClienteCarpetaPrincipal",function(data){
			console.log("Lista de clientes");
			ocultarDetalleAmortizacion();
			addEvents();
		});
	});	
		
	//Buscar cancelCliente
	$("#cancelCliente").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			console.log("Lista de clientes");
			ocultarDetalleAmortizacion();
			addEvents();
		});
	});
	
	//Boton de agregar cliente
	$("#agregarCliente").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idCliente = $("#idCliente").val();
		$("#contenido").load("/clientes/agregar",function(data){
			console.log("Formulario agregar clientes");
			ocultarDetalleAmortizacion();
			addEvents();
		});
	});
	
	//Formulario de clientes
	 $("input[name='tipoDocumento']").click(function(){
		 $("#cedulaCliente").attr('disabled', false);
		 $('#cedulaCliente').val("");
         var tipoDocumento = $("input[name='tipoDocumento']:checked").val();
         if($("#docCedula").is(':checked')) {
        	 $("#doctypeTemp").val("cedula");
        	 $('#cedulaCliente').attr('maxlength', 13);
         }else if($("#docPasaporte").is(':checked')){
        	 $('#cedulaCliente').attr('maxlength', 50);
        	 $("#doctypeTemp").val("otro");
         }
     });
	 
	//Validacion para cedula
	$('#cedulaCliente').on('keyup', function(e) { 
		if($("#docCedula").is(':checked')){
			var valor = $('#cedulaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#cedulaCliente').val(valor.substring(0, valor.length - 1));
			}
		}	
	});
	 
	$('#cedulaCliente').on('keydown', function(e) { 
		if($("#docCedula").is(':checked')){
			var valor = $('#cedulaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#cedulaCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#cedulaCliente').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#cedulaCliente').val(valor+"-");
				 }
				}
			}
		}
	});
	
	//Validacion para telefono
	$('#telefonoCliente').on('keydown', function(e) { 
			var valor = $('#telefonoCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#telefonoCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#telefonoCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#telefonoCliente').val(valor+"-");
					  }
				}
			}
	});
	
	//Validacion para celular
	$('#celularCliente').on('keydown', function(e) { 
			var valor = $('#celularCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#celularCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#celularCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#celularCliente').val(valor+"-");
					  }
				}
			}
	});
	
	//Validacion para telefono empresa
	$('#telefonoEmpresaCliente').on('keydown', function(e) { 
			var valor = $('#telefonoEmpresaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#telefonoEmpresaCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#telefonoEmpresaCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#telefonoEmpresaCliente').val(valor+"-");
					  }
				}
			}
	});
	
    $("#formClient").on("submit", function (e) {
		e.preventDefault();
		e.stopImmediatePropagation();
	        $.ajax({
	            url: "/clientes/guardar/",
	            type: "POST",
	            data: new FormData(this),
	            enctype: 'multipart/form-data',
	            processData: false,
	            contentType: false,
	            cache: false,
	            success: function (res) {
		            if(res=="INSERT"){
						Swal.fire({
							title : 'Muy bien!',
							text : 'Registro guardado',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
					}else{
						Swal.fire({
							title : 'Muy bien!',
							text : 'Registro Modificado',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
					}
		               console.log(res);
		               $("#contenido").load("/clientes/",function(data){
		        		console.log("Lista de clientes");
		        		addEvents();
		        	});
	            },
	            error: function (err) {
	                console.error(err);
	                Swal.fire({
						title : 'Error!',
						text : 'No se pudo completar la operacion, intente mas tarde',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
	            }
	        });
	});

/******************************************************* Fin Clientes ************************************************************/	
    
/******************************************************* Prestamos ***********************************************************/
	$("#prestamos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			ocultarDetalleAmortizacion();
			console.log("Lista de clientes");
			addEvents();
		});
	});
	
	$("#btnCarpetaPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		Swal.fire({
			  title: 'Ingrese la carpeta',
			  position: 'top',
			  input: 'text',
			  inputAttributes: {
			    autocapitalize: 'off'
			  },
			  showCancelButton: true,
			  confirmButtonText: 'Buscar',
			  cancelButtonText: 'Cancelar',
			  showLoaderOnConfirm: true,
			  preConfirm: (carpeta) => {
				  $.ajax({
			            url: "/carpetas/buscar/"+carpeta,
			            type: "GET",
			            processData: false,
			            contentType: false,
			            cache: false,
			            success: function (res) {
				            if(res=="No tienes carpetas creadas"){
								Swal.fire({
									title : 'No tienes carpetas creadas!',
									position : 'top',
									icon : 'warning',
									confirmButtonText : 'Cool'
								})

							}else{
								Swal.fire({
									title : 'Muy bien!',
									text : 'Carpeta Seleccionada',
									position : 'top',
									icon : 'success',
									confirmButtonText : 'Cool'
								})
							}
							$("#buscadorAgregarPrestamo").load("/prestamos/actualizarCarpeta",function(data){
								console.log("Actualizar carpeta");
								if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
									$("#cedulaPrestamo").attr('checked', 'checked');
									$("#otroPrestamo").attr('checked', false);
								}else{
									$("#otroPrestamo").attr('checked', 'checked');
									$("#cedulaPrestamo").attr('checked', false);
								}
								//Recargar combo de cuentas
								var carpeta = $("#carpetaIdPrestamo").val();
								$("#id_cuenta").load("/prestamos/actualizarCuentas/"+carpeta,function(data){
									console.log("Actualizar cuenta");
									addEvents();
									return false;
								});
							});
			            },
			            error: function (err) {
			                console.error(err);
			                Swal.fire({
								title : 'Error!',
								text : 'No se pudo completar la operacion, intente mas tarde',
								position : 'top',
								icon : 'error',
								confirmButtonText : 'Cool'
							})
			            }
			        });
			  },
			  allowOutsideClick: () => !Swal.isLoading()
			}).then((result) => {
			  if (result.isConfirmed) {
			    //Correcto
			  }
			})
	});
    
	$("#btnExitCarpetaPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#buscadorAgregarPrestamo").load("/prestamos/actualizarCarpetaPrincial",function(data){
			console.log("Actualizar carpeta");
			if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
				$("#cedulaPrestamo").attr('checked', 'checked');
				$("#otroPrestamo").attr('checked', false);
			}else{
				$("#otroPrestamo").attr('checked', 'checked');
				$("#cedulaPrestamo").attr('checked', false);
			}
			//Recargar combo de cuentas
			var carpeta = $("#carpetaIdPrestamo").val();
			$("#id_cuenta").load("/prestamos/actualizarCuentas/"+carpeta,function(data){
				console.log("Actualizar cuenta");
				addEvents();
				return false;
			});
		});
	});	
	
    $("#agregarPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		//Ocultamos la seccion de detalle de amortizacion
		mostrarDetalleAmortizacion();
		$("#contenido").load("/prestamos/agregar",function(data){
			console.log("Agregar Prestamo");
			if($("#msg").val()=="NOCLIENTE"){
				 Swal.fire({
						title : 'Alerta!',
						text : 'Debe seleccionar un cliente',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					setTimeout(function() {
						$("#contenido").load("/clientes/buscarCliente",function(data){
							console.log("Lista de clientes");
							addEvents();
						});	
					}, 1000);
			}else{
				if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
					$("#cedulaPrestamo").attr('checked', 'checked');
					$("#otroPrestamo").attr('checked', false);
				}else{
					$("#otroPrestamo").attr('checked', 'checked');
					$("#cedulaPrestamo").attr('checked', false);
				}
			}
			addEvents();
		});
	});
	
	$("#cancelarFormPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
			$("#contenido").load("/clientes/buscarCliente",function(data){
					console.log("Lista de clientes");
					addEvents();
			});	
			addEvents();
	});
	
	$("input[name='tipoDocumentoBusquedaPrestamo']").change(function(){
		 if($("#cedulaPrestamo").is(':checked')){
	    	 $('#buscarPorDocumentoPrestamo').attr('maxlength', 13);
	     }else if($("#otroPrestamo").is(':checked')){
	    	 $('#buscarPorDocumentoPrestamo').attr('maxlength', 30);
	     }
		$('#buscarPorDocumentoPrestamo').val("");
	});
	
	$('#buscarPorDocumentoPrestamo').on('keydown', function(e) { 
		if($("#cedulaPrestamo").is(':checked')){
			var valor = $('#buscarPorDocumentoPrestamo').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#buscarPorDocumentoPrestamo').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#buscarPorDocumentoPrestamo').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#buscarPorDocumentoPrestamo').val(valor+"-");
					  }
				}
			}
		}
	});
	
	$("#buscarPorDocumentoPrestamo").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
	     if(e.which == 13){
	    	 var tipoDocumento = "";
	    	 var carpeta = $("#carpetaIdPrestamo").val();
	    	 var item = $("#buscarPorDocumentoPrestamo").val();
		     if($("#cedulaPrestamo").is(':checked')){
		    	 tipoDocumento = "cedula";
		     }else if($("#otroPrestamo").is(':checked')){
		    	 tipoDocumento = "otro";
		     }
		     var longitud = item.length;
		     if(longitud > 1){
			     $("#buscadorAgregarPrestamo").load("/prestamos/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': tipoDocumento,
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);
					}else{
						$("#otroPrestamo").attr('checked', 'checked');
						$("#cedulaPrestamo").attr('checked', false);
					}
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							$("#cedulaPrestamo").attr('checked', 'checked');
							$("#otroPrestamo").attr('checked', false);
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#buscarPorNombrePrestamo").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
	     if(e.which == 13){
	    	var item = $("#buscarPorNombrePrestamo").val();
	    	var carpeta = $("#carpetaIdPrestamo").val();
	    	var longitud = item.length;
		     if(longitud > 1){
			     $("#buscadorAgregarPrestamo").load("/prestamos/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': '0',
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);
					}else{
						$("#otroPrestamo").attr('checked', 'checked');
						$("#cedulaPrestamo").attr('checked', false);
					}	
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);	
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#tipo_prestamo").change(function(e){
		if($("#tipo_prestamo").val() == 2){
			ocultarDetalleAmortizacion();
			$("#divValorCuota").hide();
			$("#divPlazos").hide();
		}else{
			mostrarDetalleAmortizacion();
			$("#divValorCuota").show();
			$("#divPlazos").show();
		}
	});
	
	$("#guardarPrestamo").on("click", (function(e){
		 e.preventDefault();
		 e.stopImmediatePropagation();
		 var datos = $("#formulario_prestamo").serializeArray();
		 var idClienteTemp = $("#idClientePrestamo").val();
		 var idCarpetaTemp = $("#carpetaIdPrestamo").val();
		 var idCuentaTemp =  $("#id_cuenta").val(); 
		 var fecha = $("#fecha").val();
		 var cantidad_pagos = $("#cantidad_pagos").val();
		 var pagos = $("#pagos").val();
		 var tipoPrestamo = $("#tipo_prestamo").val();
		 
		 if(tipoPrestamo != 2 && (parseFloat(cantidad_pagos) > parseFloat(pagos))){
			 Swal.fire({
					title : 'Advertencia!',
					text : 'La cantidad de pagos no puede ser mayor al plazo',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
		 }else{
			 datos.push( {name:'idClienteTemp', value:idClienteTemp} );
			 datos.push( {name:'idCarpetaTemp', value:idCarpetaTemp} );
			 datos.push( {name:'idCuentaTemp', value:idCuentaTemp} ); 
			 datos.push( {name:'fechaTemp', value:fecha} );
			 $.post("/prestamos/guardar", datos,
				function(data){
					console.log("Guardar Prestamo");
					 Swal.fire({
					title : 'Muy Bien!',
					text : 'Se genero el prestamo',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				$("#contenido").load("/clientes/buscarCliente",function(data){
					console.log("Lista de clientes");
					ocultarDetalleAmortizacion();
					addEvents();
				});	
			});
			return false;
			}
		 }
	));
	
	$("#aplicarCargos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		 $.get("/prestamos/cuotasNoPagadas/"+idPrestamo,
			function(data){
			console.log("Cargar cuotas no pagadas");
			$("#selectCuotaCargo").replaceWith(data);
			$("#modalAplicarCargosCuotas").modal('show');
		});
	});
	
	$("#descuentos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#selectCuotasMora").show();
		$('#tipoDescuento option:nth-child(1)').prop("selected", true).change(); 
		var idPrestamo = $("#prestamoAcct").val();
		var tipoDescuento = $("#tipoDescuento").val();
		 $.get("/prestamos/cuotasNoPagadasDescuentosTemp/"+idPrestamo+"/"+tipoDescuento,
			function(data){
				console.log("Cargar cuotas no pagadas");
				$("#selectCuotasMora").replaceWith(data);
				$("#modalDescuentos").modal('show');
		});
	});
	
	$("#tipoDescuento").change(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var tipoDescuento = $("#tipoDescuento").val();
		var tipoPrestamo = $("#tipoPrestamo").val()
		if(tipoDescuento == 1 && tipoPrestamo == 1){
			 $.get("/prestamos/cuotasNoPagadasDescuentosTemp/"+idPrestamo+"/"+tipoDescuento,
						function(data){
						$("#selectCuotasMora").replaceWith(data);
					});
		}else{
			 $.get("/prestamos/cuotasNoPagadasDescuentos/"+idPrestamo+"/"+tipoDescuento,
						function(data){
						$("#selectCuotasMora").replaceWith(data);
					});
		}
	});
	
	$("#agregaDescuento").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var montoDescuento = $("#montoDescuento").val();
		var tipoDescuento = $("#tipoDescuento").val();
		var tipoPrestamo = $("#tipoPrestamo").val()
		var cuota = $("#selectCuotasMora").val();
		
		if(!montoDescuento){
			$("#modalDescuentos").modal('hide');
			Swal.fire({
				title : 'Alerta!',
				text : 'Debe ingresar un monto',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			if(tipoPrestamo == "2" && !cuota){
				$("#modalDescuentos").modal('hide');
				Swal.fire({
					title : 'Alerta!',
					text : 'No hay datos para aplicar el descuento',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else{	
				//Validamos que el descuento no sea mayor que el monto generado
				 $.post("/prestamos/validarDescuento/",
				 {
					 "idPrestamo":idPrestamo,
					 "monto":montoDescuento,
					 "tipo":tipoDescuento,
					 "cuota":cuota
				 },function(data){
					 if(data == 1){
						 $.post("/prestamos/guardarDescuento/",{
							 "idPrestamo":idPrestamo,
							 "monto":montoDescuento,
							 "tipo":tipoDescuento,
							 "cuota":cuota
						 },function(data){
								console.log("Guardado de descuento");
								if(data == "1"){
									 Swal.fire({
											title : 'Muy bien!',
											text : 'Se ha guardado el descuento',
											position : 'top',
											icon : 'success',
											confirmButtonText : 'Cool'
										})
								}else{
									 Swal.fire({
											title : 'Alerta!',
											text : 'No se guardo el descuento',
											position : 'top',
											icon : 'warning',
											confirmButtonText : 'Cool'
										})
								}
								$("#montoDescuento").val("");
								$("#modalDescuentos").modal('hide');
								addEvents();
								cargarDetallePrestamo(idPrestamo);
						 });	
					 }else{
						 $("#modalDescuentos").modal('hide');
						 Swal.fire({
								title : 'Alerta!',
								text : 'El descuento no puede ser mayor al balance',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
						})
					 }
				});
			}
		}
	});
	
	$("#agregarCargoCuota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var motivo = $("#motivoCargoCuota").val();
		var monto = $("#montoCargoCuota").val();
		var cuota = $("#selectCuotaCargo").val();
		var nota = $("#notaCargoCuota").val();
		if(!cuota){
			cuota = 0;
		}
		
		if(!monto){
			$("#modalAplicarCargosCuotas").modal('hide');
			Swal.fire({
				title : 'Alerta!',
				text : 'Debe ingresar un monto',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			 $.post("/prestamos/guardarCargoCuota/",{
				 "idPrestamo":idPrestamo,
				 "motivo":motivo,
				 "monto":monto,
				 "cuota":cuota,
				 "nota":nota
			 },function(data){
					console.log("Guardado de cargo");
					if(data == "1"){
						 Swal.fire({
								title : 'Muy bien!',
								text : 'Se ha guardado el cargo',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
					}else{
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se guardo el cargo',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
					}
					$("#motivoCargoCuota").val("");
					$("#montoCargoCuota").val("");
					$("#modalAplicarCargosCuotas").modal('hide');
					addEvents();
					cargarDetallePrestamo(idPrestamo);
			 });
		}
	});
	
	$("#recibirAbono").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var tipoPrestamoAcct = $("#tipoPrestamo").val();
		if(tipoPrestamoAcct == 2){
			$("#optionCuotasCapital").hide();
		}else{
			$("#optionCuotasCapital").show();
		}
		var idPrestamoAcct = $("#prestamoAcct").val();
		
		if($("#tablaPagos").length){
			$("#tablaPagos").load("/prestamos/cargarPagoTemp/"+idPrestamoAcct,function(data){
				addEvents();
				montoTotalAbono();
				$("#montoAbonoCuota").val("");
			});
		}
		
		$("#modalRecibirAbonoCuotas").modal('show');
	});
	
	$("#agregarAbonoCuota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var monto = $("#montoAbono").val();
		var tipoCuota = $("#selectTipoCuotaAbono").val();
		var balancePendiente = parseFloat($("#capitalPendienteTemp").val());
		
		var totalesCuota = parseFloat($("#totalesCuotas").text());
		
		if(monto > totalesCuota){
			Swal.fire({
				title : 'Alerta!',
				text : 'El monto no puede ser mayor al total',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
			$("#modalRecibirAbonoCuotas").modal('hide');
		}else{
			if(!monto || monto<1){
				$("#modalRecibirAbonoCuotas").modal('hide');
				Swal.fire({
					title : 'Alerta!',
					text : 'Debe ingresar un monto',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else{
//				if(monto>balancePendiente){
//					$("#modalRecibirAbonoCuotas").modal('hide');
//					Swal.fire({
//						title : 'Alerta!',
//						text : 'El monto a pagar no puede ser mayor que el balance pendiente',
//						position : 'top',
//						icon : 'warning',
//						confirmButtonText : 'Cool'
//					})
//				}else{
					 $.post("/prestamos/guardarAbonoCuota/",{
						 "idPrestamo":idPrestamo,
						 "monto":monto,
						 "tipoCuota":tipoCuota
					 },function(data){
							console.log("Guardado de cargo");
							if(data > 0){
								 Swal.fire({
										title : 'Muy bien!',
										text : 'Se ha guardado el abono',
										position : 'top',
										icon : 'success',
										confirmButtonText : 'Cool'
									})
								imprimirAbonoDetalle(data);	
							}else{
								 Swal.fire({
										title : 'Alerta!',
										text : 'No se guardo el abono',
										position : 'top',
										icon : 'warning',
										confirmButtonText : 'Cool'
									})
							}
							$("#archivoPago").val("");
							$("#montoAbonoCuota").val("");
							$("#modalRecibirAbonoCuotas").modal('hide');
							addEvents();
							cargarDetallePrestamo(idPrestamo);
					 });
//				}
			}
		}
		
	});

	$("#eliminarPagosTemp").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var id = $("#prestamoAcct").val();
		 $.get("/prestamos/eliminarPagosTemp/"+id,
			function(data){
			 $("#archivoPago").val("");
			 $("#montoAbonoCuota").val("");
			 $("#modalRecibirAbonoCuotas").modal("hide");
			 	addEvents();
		 	});
	});
	
	$("#crearNota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var prestamoAcct = $("#prestamoAcct").val();
		//Cargamos el combo de cuotas
		$("#cuotaNota").load("/prestamos/mostrarCotasPrestamo/"+prestamoAcct,function(data){
			console.log("Cuotas");
			addEvents();
		});
		$("#modalAgregarNota").modal('show');
	});
	
	$("#btnAgregarNota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var fecha = $("#fechaNota").val();
		var nota = $("#valorNota").val();
		var cuota = $("#cuotaNota").val(); 
		var prestamoAcct = $("#prestamoAcct").val();

		 $.post("/notas/guardarNota",{
			 "idPrestamo":prestamoAcct,
			 "nota":nota,
			 "fecha":fecha,
			 "cuota":cuota
		 },function(data){
				console.log("Guardado de Nota");
				if(data == "1"){
					 Swal.fire({
							title : 'Muy bien!',
							text : 'Se ha guardado la nota',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
				}else{
					 Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la nota',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
				}
				$("#modalAgregarNota").modal('hide');
				$("#valorNota").val("");
				verNotas(prestamoAcct);
		 });
		
	});
/****************************************************** Fin Prestamos *******************************************************/

/******************************************************** Caja **************************************************************/

$("#caja").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccion1Only();
	$("#contenido").load("/cajas/mostrarCuadre",function(data){
		console.log("Cuadre de Caja");
		addEvents();
	});
});

$("#fechaCuadreCaja").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	var fecha = $("#fechaCuadreCaja").val();
	var user = $("#usuarioCuadreCaja").val();
	$("#contenido").load("/cajas/mostrarCuadre",
		{
			"fecha" : fecha,
			"userId" : user
		},function(data){
		var userAcctCaja = $("#userAcctCaja").val();	
		$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
		console.log("Cuadre de Caja");
		addEvents();
	});
});

$("#usuarioCuadreCaja").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	var fecha = $("#fechaCuadreCaja").val();
	var user = $("#usuarioCuadreCaja").val();
	$("#contenido").load("/cajas/mostrarCuadre",
		{
			"fecha" : fecha,
			"userId" : user
		},function(data){
		var userAcctCaja = $("#userAcctCaja").val();	
		$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
		console.log("Cuadre de Caja");
		addEvents();
	});
});

/****************************************************** Fin Caja **********************************************************/

/****************************************************** Contabilidad **************************************************************/

$("#contabilidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
		console.log("Contabilidad");
		addEvents();
	});
});

$("#editCodigoCuenta").on('keyup', function(e) { 
	e.stopImmediatePropagation();
	
	var valor = $('#editCodigoCuenta').val();
	var res = valor.match(/^[0-9-]+$/);
		
	if(res == null){
		 $('#editCodigoCuenta').val(valor.substring(0, valor.length - 1));
	}

	var codigoCuenta = $("#editCodigoCuenta").val();
	var cant = codigoCuenta.length;
	if(cant == 1){
		$.get("/grupoCuentas/buscarGrupoCuenta/"+codigoCuenta,
			function(data){
				if(data != "0"){
					$("#editGrupoCuenta").val(data);
				}else{
					$("#editGrupoCuenta").val("");
				}
			addEvents();
		});
	}
	
	if(cant == 0){
		$("#editGrupoCuenta").val("");
	}
	
	if(cant > 1){
		$.get("/cuentasContables/buscarCuentaControl/"+codigoCuenta,
				function(data){
					if(data[1] != "0"){
						$("#editCuentaControl").val(data[0]);
						$("#idEditCuentaControl").val(data[1]);
					}else{
						$("#editCuentaControl").val("");
						$("#idEditCuentaControl").val("0");
					}
			addEvents();
		});
	}
});

$("#codigoCuenta").on('keyup', function(e) { 
	e.stopImmediatePropagation();
	
	var valor = $('#codigoCuenta').val();
	var res = valor.match(/^[0-9-]+$/);
		
	if(res == null){
		 $('#codigoCuenta').val(valor.substring(0, valor.length - 1));
	}

	var codigoCuenta = $("#codigoCuenta").val();
	var cant = codigoCuenta.length;
	if(cant == 1){
		$.get("/grupoCuentas/buscarGrupoCuenta/"+codigoCuenta,
			function(data){
				if(data != "0"){
					$("#grupoCuenta").val(data);
				}else{
					$("#grupoCuenta").val("");
				}
			addEvents();
		});
	}
	
	if(cant == 0){
		$("#grupoCuenta").val("");
	}
	
	if(cant > 1){
		$.get("/cuentasContables/buscarCuentaControl/"+codigoCuenta,
				function(data){
					if(data[1] != "0"){
						$("#cuentaControl").val(data[0]);
						$("#idCuentaControl").val(data[1]);
					}else{
						$("#cuentaControl").val("");
						$("#idCuentaControl").val("0");
					}
			addEvents();
		});
	}
});

$("#btnCrearCuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
		console.log("Cuentas Contables");
		$("#modalCuentas").modal("show");
		addEvents();
	});
});

$("#agregarCuentaContable").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var tipoCuenta = $("#tipoCuenta").val();
	var grupoCuenta = $("#grupoCuenta").val();
	var codigoCuenta = $("#codigoCuenta").val();
	var nombreCuenta = $("#nombreCuenta").val();
	var cuentaControl = $("#cuentaControl").val();
	var idCuentaControl = $("#idCuentaControl").val();
	if(tipoCuenta != 0 && grupoCuenta!="" && nombreCuenta!=""){
		 $.post("/cuentasContables/crear",{
			 "tipoCuenta":tipoCuenta,
			 "grupoCuenta":grupoCuenta,
			 "codigoCuenta":codigoCuenta,
			 "nombreCuenta":nombreCuenta,
			 "cuentaControl":cuentaControl,
			 "idCuentaControl":idCuentaControl
		 },function(data){
				$("#grupoCuenta").val("");
				$("#codigoCuenta").val("");
				$("#nombreCuenta").val("");
				$("#cuentaControl").val("");
				$("#idCuentaControl").val("");
			 	if(data == "1"){
			 		$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
			 			console.log("Cuentas Contables");
			 			addEvents();
			 		});
			 	}else if(data == "-1"){
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else if(data == "-2"){
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar cuentas a cuentas auxiliares",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else if(data == "-3"){
			 		$("#modalCuentas").modal("hide");
			 		Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else{
			 		//No guardo
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se guardo la cuenta contable",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}
				addEvents();
		 });
	}else{
		$("#modalCuentas").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El nombre, el tipo y el Grupo cuenta no pueden estar vacio",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalCuentas").modal("show");
			  }
			})
	}
});

$("#btnGuardarUpdateCuentaContable").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idEditCuentaControlPrincipal").val();
	var codigoCuenta = $("#editCodigoCuenta").val();
	var nombreCuenta = $("#editNombreCuenta").val();
	var tipoCuenta = $("#editTipoCuenta").val();
	var cuentaControl = $("#editCuentaControl").val();
	var grupoCuenta = $("#editGrupoCuenta").val();
	var idCuentaControl = $("#idEditCuentaControl").val();
	
	if(tipoCuenta != 0 && grupoCuenta!="" && nombreCuenta!=""){
		 $.post("/cuentasContables/modificar",{
			 "id":id,
			 "tipoCuenta":tipoCuenta,
			 "grupoCuenta":grupoCuenta,
			 "codigoCuenta":codigoCuenta,
			 "nombreCuenta":nombreCuenta,
			 "cuentaControl":cuentaControl,
			 "idCuentaControl":idCuentaControl
		 },function(data){
			 	if(data == "1"){
			 		$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
			 			console.log("Cuentas Contables");
			 			addEvents();
				 		$("#modalModificarCuentas").modal("hide");
						Swal.fire({
							  title: 'Muy Bien!',
							  text: "Registro modificado",
							  icon: 'success',
							  position : 'top',
							  showCancelButton: false,
							  confirmButtonColor: '#3085d6',
							  confirmButtonText: 'Ok!'
							}).then((result) => {
							  if (result.isConfirmed) {
								$("#modalCuentas").modal("show");
							  }
							})
			 		});
			 	}else if(data == "-1"){
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}else if(data == "-2"){
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar cuentas a cuentas auxiliares",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}else if(data == "-3"){
			 		$("#modalModificarCuentas").modal("hide");
			 		Swal.fire({
						  title: 'Alerta!',
						  text: "No puede modificar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else{
			 		//No guardo
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se guardo la cuenta contable",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}
				addEvents();
		 });
	}else{
		$("#modalModificarCuentas").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El nombre, el tipo y el Grupo cuenta no pueden estar vacio",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalModificarCuentas").modal("show");
			  }
			})
	}
});
/******************************************************Fin Contabilidad **********************************************************/

/******************************************************** Usuarios **********************************************************/

$("#usuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/usuarios/listaUsuarios",function(data){
		console.log("Agregar Usuario");
		addEvents();
	});
});

$("#listaUsuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/usuarios/listaUsuarios",function(data){
		console.log("lista Usuario");
		addEvents();
	});
});

$("#agregarUsuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/usuarios/agregarUsuarios",function(data){
		console.log("Agregar Usuario");
		addEvents();
	});
});

$("#formAgregarUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formAgregarUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/crear",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario creado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se guardo el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});

$("#formModificarPasswordUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formModificarPasswordUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/modificarPassword",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se modifico el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});

$("#formModificarUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formModificarUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/modificar",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se modifico el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});
/******************************************************** Fin Usuarios **********************************************************/

/******************************************************** Cobros **********************************************************/

$("#cobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/prestamos/prestamosPendientes",function(data){
		console.log("Prestamos pendientes");
		addEvents();
	});
});

$("#btnCarpetaCobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	Swal.fire({
		  title: 'Ingrese la carpeta',
		  position: 'top',
		  input: 'text',
		  inputAttributes: {
		    autocapitalize: 'off'
		  },
		  showCancelButton: true,
		  confirmButtonText: 'Buscar',
		  cancelButtonText: 'Cancelar',
		  showLoaderOnConfirm: true,
		  preConfirm: (carpeta) => {
			  $.ajax({
		            url: "/carpetas/buscarCarpetaCobros/"+carpeta,
		            type: "GET",
//		            data: "",
//		            enctype: 'multipart/form-data',
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
//								text : 'No tienes carpetas creadas',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
								{
									
								},function(data){
								console.log("Lista de clientes");
								ocultarDetalleAmortizacion();
								addEvents();
							});
						}
		            },
		            error: function (err) {
		                console.error(err);
		                Swal.fire({
							title : 'Error!',
							text : 'No se pudo completar la operacion, intente mas tarde',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'Cool'
						})
		            }
		        });
		  },
		  allowOutsideClick: () => !Swal.isLoading()
		}).then((result) => {
		  if (result.isConfirmed) {
		    //Correcto
		  }
		})
});

$("#tipoPago").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var tipoPago = $("#tipoPago").val();
	if(tipoPago == "2" || tipoPago == "3"){
		 $("#imagenTransaccionAbono").show();
	}else{
		 $("#imagenTransaccionAbono").hide();
	}
});

$("#agregarPagoTemp").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var prestamoAcct = $("#prestamoAcct").val(); 
	$("#idPrestamoAbonoNota").val(prestamoAcct); 
	var form = $('#formAbonosNotas')[0];
	console.log();
	  $.ajax({
          url: "/prestamos/crearPagoTemp",
          type: "POST",
          data: new FormData(form),
          enctype: 'multipart/form-data',
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
          	$("#tablaPagos").replaceWith(res);
	        addEvents();
	        montoTotalAbono();
	        $("#montoAbonoCuota").val("");
	        $("#archivoPago").val("");
          },
          error: function (err) {
              console.error(err);
              Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
          }});
   });

$("#selectEstadoCuota").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var estado = $("#selectEstadoCuota").val();
	$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
		{
			"estado": estado
		},function(data){
		console.log("Lista de prestamos del cliente");
		addEvents();
		var estadoCuotaHidden = $("#estadoCuotaHidden").val();
		$("#selectEstadoCuota option[value="+ estadoCuotaHidden +"]").attr("selected",true);
	});
});

$('#nombreOCedula').on('keyup', function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var item = $('#nombreOCedula').val();
	var estado = $("#selectEstadoCuota").val();
	var longitud = item.length;
	if(longitud > 2){
		$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
			{
				"estado": estado,
				"item": item
			},function(data){
			console.log("Lista de prestamos del cliente");
			addEvents();
		});
	}else{
		$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
				{
					"estado": estado,
					"item": 0
				},function(data){
				console.log("Lista de prestamos del cliente");
				addEvents();
			});
	}
});

$("#btnExitCarpetaCobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaCobros",
          type: "GET",
//          data: "",
//          enctype: 'multipart/form-data',
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
						{
							
						},function(data){
						console.log("Lista de clientes");
						ocultarDetalleAmortizacion();
						addEvents();
					});
				}
          },
          error: function (err) {
              console.error(err);
              Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
          }
      });
});	

/******************************************************* Fin Cobros **********************************************************/

/******************************************************** Empleados *********************************************************/

$("#empleados").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empleados/listaEmpleados",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empleados");
		addEvents();
	});
});

$("#listaEmpleados").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empleados/listaEmpleados",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empleados");
		addEvents();
	});
});

$("#agregarEmpleado").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empleados/agregarEmpleado",function(data){
		ocultarDetalleAmortizacion();
		console.log("Agregar empleados");
		addEvents();
	});
});

$("#formEmpleado").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formEmpleado").serializeArray();
	
//	if(){
//		datos.push( {name:'id', value:id} );
//	}
	
	console.log(datos);
	 $.post("/empleados/guardar",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Empleado creado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El empleado ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se guardo el empleado',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "4"){
				Swal.fire({
					title : 'Muy bien!',
					text : 'Empleado modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/empleados/agregarEmpleado",function(data){
				console.log("Agregar Empleado");
				addEvents();
			});
		});
});

$("#agregarAsignacion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idEmpleado = $("#idEmpleadoAsignacion").val();
	var motivoAsignacion = $("#motivoAsignacion").val();
	var montoAsignacion = $("#montoAsignacion").val();
	
	if(!montoAsignacion || !motivoAsignacion){
		$("#modalAsignacion").modal("hide");
		Swal.fire({
			title : 'Alerta!',
			text : 'Los campos no pueden estar vacios',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		$("#asignacionesEmpleado").load("/empleados/guardarAsignacion",
				{
					"idEmpleado" : idEmpleado,
					"motivo" : motivoAsignacion,
					"monto" : montoAsignacion
				},function(data){
					$("#modalAsignacion").modal("hide");
					if(data == "0"){
						Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la asignacion',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
					}else{
						$("#modalAsignacion").modal("show");
						console.log("Guardar Asignacion");
						$("#asignacionesEmpleado").load("/empleados/listaAsignaciones/"+idEmpleado,function(data){
							$("#motivoAsignacion").val("");
							$("#montoAsignacion").val("");
							console.log("Agregar Asignacion");
						});
					}

		});
	}
});

$("#agregarDeduccion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idEmpleado = $("#idEmpleadoDeduccion").val();
	var motivoDeduccion = $("#motivoDeduccion").val();
	var montoDeduccion = $("#montoDeduccion").val();
	
	if(!montoDeduccion || !motivoDeduccion){
		$("#modalDeduccion").modal("hide");
		Swal.fire({
			title : 'Alerta!',
			text : 'Los campos no pueden estar vacios',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		$("#deduccionesEmpleado").load("/empleados/guardarDeduccion",
				{
					"idEmpleado" : idEmpleado,
					"motivo" : motivoDeduccion,
					"monto" : montoDeduccion
				},function(data){
					$("#modalDeduccion").modal("hide");
					if(data == "0"){
						Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la deduccion',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
					}else{
						$("#modalDeduccion").modal("show");
						console.log("Guardar deduccion");
						$("#deduccionesEmpleado").load("/empleados/listaDeducciones/"+idEmpleado,function(data){
							$("#motivoDeduccion").val("");
							$("#montoDeduccion").val("");
						});
					}

		});
	}
});

/******************************************************** Fin Empleados *****************************************************/

/******************************************************** Empresas **********************************************************/

$("#empresas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empresas/listaEmpresas",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empresas");
		addEvents();
	});
});

$("#agregarEmpresa").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empresas/agregarEmpresa",function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario de empresas");
		addEvents();
	});
});

$("#formAddEmpresa").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/empresas/crear/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro guardado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	               $("#contenido").load("/empresas/agregarEmpresa",function(data){
	        		addEvents();
	        	});
            },
            error: function (err) {
                console.error(err);
                Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
            }
        });
});

$("#formUpdateEmpresa").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/empresas/modificar/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro modificado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo modificar el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	               $("#contenido").load("/empresas/agregarEmpresa",function(data){
	        		addEvents();
	        	});
            },
            error: function (err) {
                console.error(err);
                Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
            }
        });
});

/******************************************************** Fin Empresas ******************************************************/

/******************************************************** Cuentas ***********************************************************/

$("#cuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/cuentas/listaCuentas",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de cuentas");
		addEvents();
	});
});

$("#btnCarpetaCuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	Swal.fire({
		  title: 'Ingrese la carpeta',
		  position: 'top',
		  input: 'text',
		  inputAttributes: {
		    autocapitalize: 'off'
		  },
		  showCancelButton: true,
		  confirmButtonText: 'Buscar',
		  cancelButtonText: 'Cancelar',
		  showLoaderOnConfirm: true,
		  preConfirm: (carpeta) => {
			  $.ajax({
		            url: "/carpetas/buscarCarpetaCuentas/"+carpeta,
		            type: "GET",
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
//								text : 'No tienes carpetas creadas',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#contenido").load("/cuentas/listaCuentas",function(data){
								ocultarDetalleAmortizacion();
								console.log("Lista de cuentas");
								addEvents();
							});
						}
		            },
		            error: function (err) {
		                console.error(err);
		                Swal.fire({
							title : 'Error!',
							text : 'No se pudo completar la operacion, intente mas tarde',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'Cool'
						})
		            }
		        });
		  },
		  allowOutsideClick: () => !Swal.isLoading()
		}).then((result) => {
		  if (result.isConfirmed) {
		    //Correcto
		  }
		})
});

$("#btnExitCarpetaCuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaCuentas",
          type: "GET",
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#contenido").load("/cuentas/listaCuentas",function(data){
						ocultarDetalleAmortizacion();
						console.log("Lista de cuentas");
						addEvents();
					});
				}
          },
          error: function (err) {
              console.error(err);
              Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
          }
      });
});	

$("#agregarCuenta").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/cuentas/agregarCuenta",function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario de cuentas");
		addEvents();
	});
});

$("#formAddCuenta").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/cuentas/crear/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro guardado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	            $("#contenido").load("/cuentas/agregarCuenta",function(data){
	            	addEvents();
	            });
            },
            error: function (err) {
                console.error(err);
                Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
            }
        });
});

$("#formUpdateCuenta").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/cuentas/modificar/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro modificado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo modificar el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	            $("#contenido").load("/cuentas/agregarCuenta",function(data){
	            	addEvents();
	            });
            },
            error: function (err) {
                console.error(err);
                Swal.fire({
					title : 'Error!',
					text : 'No se pudo completar la operacion, intente mas tarde',
					position : 'top',
					icon : 'error',
					confirmButtonText : 'Cool'
				})
            }
        });
});

/******************************************************** Fin Cuentas *******************************************************/
}


/****************************************************** Funciones ***********************************************************/

function seleccionarCliente(id){
	 $("#contenido").load("/clientes/getInfoCliente/"+id,
			function(data){
				console.log("Buscar clientes");
				if($("#msg").val()== "No se encontro el cliente"){
					 Swal.fire({
							title : 'Alerta!',
							text : 'No se encontro al cliente',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
				}else{
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}
				}
				addEvents();
			});
}

function modificarCliente(id){
	$("#contenido").load("/clientes/modificar/"+id,function(data){
		console.log("Modificar Cliente");
		addEvents();
	});
}

function amortizar(){
	 var datos = $("#formulario_prestamo").serializeArray();
	 var fecha = $("#fecha").val();
	 datos.push( {name:'fechaTemp', value:fecha} );
	 $.post("/prestamos/amortizar",datos,
		function(data){
			console.log("Amortizar");
			$("#cuerpo_amortizacion").replaceWith(data);
			addEvents();
	 	});
}

function calCuota(){
	var monto=$("#monto").val();
	var pagos=$("#pagos").val();
	var tasa=$("#tasa").val();
	
	//Interes
	if($("#tipo_prestamo").val()==2){
		var interes = monto * (parseFloat(tasa) / 100);
		$("#valor_interes").val(interes);
	}else{
		  if(parseFloat(monto) > 0 && parseFloat(pagos) && parseFloat(tasa)){
			  $.post("/prestamos/calculoCuota",$("#formulario_prestamo").serialize(),
					function(data){
						console.log("Calculo Cuota");
						$("#calculosCuota").replaceWith(data);
						$("#valor_cuota").val($("#responseCuota").val());
					    $("#valor_interes").val($("#responseInteres").val());   
						$("#total_pagar").val($("#responseTotalPagar").val());
						
						if(parseFloat(tasa) > 0 && parseFloat(pagos) > 0 && parseFloat(tasa)){
							console.log("entre en amortizar");
							amortizar();
						}
				});
		  }
	}
}

function calTasa(){
	var monto=$("#monto").val();
	var pagos=$("#pagos").val();
	var cuota=$("#valor_cuota").val();
	
	if(monto && pagos && cuota){ 
		  $.post("/prestamos/calculoTasa",$("#formulario_prestamo").serialize(),
				function(data){
					console.log("Calculo Tasa");
					$("#calculosTasa").replaceWith(data);
				    $("#valor_interes").val($("#responseTasaInteres").val()); 
				    $("#tasa").val($("#responseTasa").val());
					$("#total_pagar").val($("#responseTasaTotalPagar").val());
					amortizar();
			});
	  }
}

function eliminarCliente(id){
	Swal.fire({
		  title: 'Esta seguro?',
		  text: "Esta accion es irreversible!",
		  icon: 'warning',
		  position: 'top',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si, Eliminar!',
		  cancelButtonText: 'Cancelar',
		}).then((result) => {
		  if (result.value) {
		    Swal.fire({
			  icon: 'success',
			  title: 'Muy bien!',
			  text: 'El registro se borrara.!',
			  position: 'top'
			})
		    
			$("#contenido").load("/clientes/eliminar/"+id,function(data){
				console.log("Eliminar Cliente");
				addEvents();
			});
		  }
		})
}

function cargarDetallePrestamo(id){
	 $.get("/prestamos/detalleAmortizacion/"+id,
		function(data){
			console.log("Detalle Amortizacion");
			mostrarDetalleAmortizacion();
			$("#cuerpo_amortizacion").replaceWith(data);
			$("#prestamoAcct").val(id);
				if($("#tipoPrestamo").val()=='2'){
					//Interes
					$("#thMontoCuota").hide();
					$("#thMontoCapital").hide();
					$("#thInteresGenerado").text("Int/Gen");
					$("#thInteres").text("Int/Hoy");
					$("#thInteresGenerado").show();
				}else{
					//Cuotas
					$("#thMontoCuota").show();
					$("#thMontoCapital").show();
					$("#thInteres").text("Interes");
					$("#thInteresGenerado").hide();
				}
				addEvents();
			$("#botonesAccionPrestamo").show();
	 	});
}

function checkCuota(idDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	var tipoCuota = $("#selectTipoCuotaAbono").val();
	var selected = '';    
    $('.checkCuota').each(function(){
        if (this.checked) {
            selected += $(this).val()+', ';
        }
    }); 

    if (selected == '') {
    	$("#totalMora").text("");
    	$("#totalCargo").text("");
    	$("#totalDescuento").text("");
    	$("#totalBalance").text("");
    }else{
    	//Cargar precios totales
		 $.post("/prestamos/totalesCuotas/",{
			 "idPrestamo":idPrestamo,
			 "cuotas":selected,
			 "tipoCuota":tipoCuota
		 },function(data){
				console.log("Montos totales Cutotas");
				$("#totalesGeneralesPrestamo").replaceWith(data);
				addEvents();
		 });
    }
}

function cargarDetalleCargo(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#tablaCargos").load("/prestamos/detallesCargos/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los cargos");
		$("#modalDetalleCargos").modal("show");
	});
}

function cargarDetalleMora(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#tablaMoras").load("/prestamos/detallesMoras/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de las moras");
		$("#modalDetalleMoras").modal("show");
	});
}

function cargarDetalleCapital(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#capitalInfo").load("/prestamos/detallesCapitales/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los capitales");
		$("#modalDetalleCapitales").modal("show");
	});
}

function cargarDetalleInteres(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#interesInfo").load("/prestamos/detallesIntereses/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los Intereses");
		$("#modalDetalleIntereses").modal("show");
	});
}

function versubcat(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat2(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria2-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria2-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat3(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria3-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria3-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat4(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria4-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria4-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function modificarUsuario(id){
	$("#contenido").load("/usuarios/modificarUsuario/"+id,function(data){
		console.log("Formulario Modificar Usuario");
		addEvents();
	});
}

function cambiarPassword(id){
	$("#contenido").load("/usuarios/cambiarPassword/"+id,function(data){
		console.log("Formulario Cambiar Password");
		addEvents();
	});
}

function verNotas(id){
	$("#contenido").load("/prestamos/verNotas/"+id,function(data){
		console.log("Ver Notas");
		var idPrestamo = $("#prestamoAcct").val();
		addEvents();
		cargarDetallePrestamo(idPrestamo);
	});
}

function eliminarPagoTemp(id){
	$("#tablaPagos").load("/prestamos/eliminarPagoTemp/"+id, function(data){
		montoTotalAbono();
		addEvents();
	});
}

function montoTotalAbono(){
	var idPrestamo = $("#prestamoAcct").val();
	$("#montoTotalAbono").load("/prestamos/montoTotalAbono/"+idPrestamo, function(data){
		addEvents();
	});
}

function verInfoNotas(id){
	$("#tablaNotas").load("/notas/listaNotas/"+id,function(data){
		console.log("Ver Notas");
		$("#modalDetalleNotas").modal("show");
		addEvents();
	});
}

function imprimirAbonoDetalle(id){
	window.open("/prestamos/imprimirDetalleAbono/"+id);
}

function verEmpleado(id){
	$("#datosEmpleado").load("/empleados/infoEmpleado/"+id,function(data){
		$("#modalEmpleado").modal("show");
	});
}

function modificarEmpleado(id){
	$("#contenido").load("/empleados/modificarEmpleado/"+id,function(data){
		console.log("Modificar Empleado");
		addEvents();
	});
}

function asignarDeduccion(id){
	$("#deduccionesEmpleado").load("/empleados/listaDeducciones/"+id,function(data){
		$("#idEmpleadoDeduccion").val(id);
		$("#motivoDeduccion").val("");
		$("#montoDeduccion").val("");
		$("#modalDeduccion").modal("show");
		console.log("Agregar Deduccion");
	});
}

function asignarAsignacion(id){
	$("#asignacionesEmpleado").load("/empleados/listaAsignaciones/"+id,function(data){
		$("#idEmpleadoAsignacion").val(id);
		$("#motivoAsignacion").val("");
		$("#montoAsignacion").val("");
		$("#modalAsignacion").modal("show");
		console.log("Agregar Asignacion");
	});
}

function eliminarAsignacion(id){
	$("#modalAsignacion").modal("hide");
	
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				$("#modalAsignacion").modal("hide");
				$("#asignacionesEmpleado").load("/empleados/eliminarAsignacion/"+id,function(data){
					$("#motivoAsignacion").val("");
					$("#montoAsignacion").val("");
					console.log("Eliminar Asignacion");
				}); 
				Swal.fire({
					title : 'Borrado!',
					text : 'Se elimindo el registro',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
		  }
		})
}

function eliminarDeduccion(id){
	$("#modalDeduccion").modal("hide");
	
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				$("#modalDeduccion").modal("hide");
				$("#deduccionesEmpleado").load("/empleados/eliminarDeduccion/"+id,function(data){
					$("#motivoDeduccion").val("");
					$("#montoDeduccion").val("");
					console.log("Eliminar Deduccion");
				}); 
				Swal.fire({
					title : 'Borrado!',
					text : 'Se elimindo el registro',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
		  }
		})
}

function modificarEmpresa(id){
	$("#contenido").load("/empresas/modificarEmpresa/"+id,function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario modificar empresas");
		addEvents();
	});
}

function modificarCuenta(id){
	$("#contenido").load("/cuentas/modificarCuenta/"+id,function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario modificar cuentas");
		addEvents();
	});
}

function modificarCuentaContable(id){
	$("#modalCuentas").modal('hide');
	$("#editCuentaContable").load("/cuentasContables/modificarCuentaContable/"+id,function(data){
		$("#modalModificarCuentas").modal('show');
		addEvents();
	});
}

function eliminarCuentaContable(id){
	$("#modalCuentas").modal('hide');
	$("#modalModificarCuentas").modal('hide');
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				 $.post("/cuentasContables/eliminar/",
						 {
							 "idCuentaContable":id
						 },function(data){
							 if(data == 1){
								 $("#modalCuentas").modal('hide');
									Swal.fire({
									title: 'Muy bien!',
									text: "Registro eliminado",
									icon: 'success',
									position : 'top',
									showCancelButton: false,
									 confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
									 			console.log("Cuentas Contables");
									 			addEvents();
												$("#modalCuentas").modal("show");
									 	});
									  }
									})
							 }else{
								$("#modalCuentas").modal('hide');
								$("#modalModificarCuentas").modal('hide');
								Swal.fire({
								title: 'Alerta!',
								text: "No se pudo eliminar el registro",
								icon: 'warning',
								position : 'top',
								showCancelButton: false,
								 confirmButtonColor: '#3085d6',
								  confirmButtonText: 'Ok!'
								}).then((result) => {
								  if (result.isConfirmed) {
									$("#modalCuentas").modal("show");
								  }
							})
						 }
				});
		  }else{
			  $("#modalCuentas").modal("show");
		  }
		})
}

function styleSeccion1Only(){
	$("#cuerpo").css("min-height", "420px");
	$("#cuerpo").css("overflow", "hidden");
	$("#cuerpo").css("height", "auto");
	$("#cuerpo").css("max-height", "auto");
	
	$("#contenido").css("min-height", "400px");
	$("#contenido").css("overflow", "hidden");
	$("#contenido").css("height", "auto");
	$("#contenido").css("max-height", "auto");
	
	$("#formularioCuerpo").css("min-height", "400px");
	$("#formularioCuerpo").css("overflow", "hidden");
	$("#formularioCuerpo").css("height", "auto");
	$("#formularioCuerpo").css("max-height", "auto");
}

function mostrarDetalleAmortizacion(){
	$("#cuerpoTabla").show();
}

function ocultarDetalleAmortizacion(){
	$("#cuerpoTabla").hide();
}
/******************************************************* Fin Funciones ******************************************************/
