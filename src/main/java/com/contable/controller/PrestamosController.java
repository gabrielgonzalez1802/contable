package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Cuenta;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoDetalleMixto;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.ICuentasService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;

@Controller
@RequestMapping("/prestamos")
public class PrestamosController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private ICuentasService serviceCuentas;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IPrestamosAdicionalesService servicePrestamosAdicionales;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/")
	public String listaPrestamos(Model model) {
		return "prestamos/listaPrestamos :: listaPrestamo";
	}
	
	@GetMapping("/agregar")
	public String agregarPrestamos(Model model, HttpSession session) {
		Prestamo prestamo = new Prestamo();
		Cliente cliente = new Cliente();
		prestamo.setCuenta(new Cuenta());
		Carpeta carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		if(!cuentas.isEmpty()) {
			for (Cuenta cuenta : cuentas) {
				cuenta.setBanco(cuenta.getBanco()+" - "+formato2d(cuenta.getMonto()));
			}
		}
		if((Integer)session.getAttribute("cliente")==0) {
			model.addAttribute("prestamo",  new Prestamo());
			model.addAttribute("cliente", cliente);
			model.addAttribute("carpeta", carpeta);
			model.addAttribute("cuentas", cuentas);
			model.addAttribute("msg", "NOCLIENTE");
			model.addAttribute("tipoDocumentoAcctPrestamo", "cedula");
			return "prestamos/form :: form";
		}else {
			cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cuentas", cuentas);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		model.addAttribute("prestamo",  new Prestamo());
		return "prestamos/form :: form";
	}
	
	@GetMapping("/actualizarCarpeta")
	public String actualizarCarpeta(Model model, HttpSession session) {
		Carpeta carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		Cliente cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
	}
	
	@GetMapping("/actualizarCarpetaPrincial")
	public String actualizarCarpetaPrincial(Model model, HttpSession session) {
		Carpeta carpeta = serviceCarpetas.buscarTipoCarpeta(1).get(0);
		Cliente cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
	}
	
	@GetMapping("/actualizarCuentas/{id}")
	public String actualizarCuentas(Model model, @PathVariable(name = "id") Integer idCarpeta) {
		Carpeta carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		model.addAttribute("cuentas", cuentas);
		return "prestamos/form :: #id_cuenta";
	}
	
	@PostMapping("/getInfoCliente")
	public String getInfoCliente(Model model, Integer carpeta, String tipoDocumento, String item, HttpSession session) {
		Cliente cliente = new Cliente();
//		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		Carpeta carpetaTemp = serviceCarpetas.buscarPorId(carpeta);
		session.setAttribute("carpeta", carpetaTemp.getId());
		model.addAttribute("carpeta", carpetaTemp);
		if (tipoDocumento.equals("cedula")) {
			cliente = serviceClientes.buscarPorCedula(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else if(tipoDocumento.equals("otro")){
			cliente = serviceClientes.buscarPorOtro(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else {
			//busqueda por nombre
			List<Cliente> clientes = serviceClientes.buscarPorNombre(item).stream().
					filter(c -> c.getEstado() == 1).collect(Collectors.toList());
			if(clientes.isEmpty()) {
				cliente = null;
			}else {
				if(clientes.size()>1) {
					model.addAttribute("clientes", clientes);
					return "clientes/infoCliente :: infoClienteLista";
				}else {
					cliente = clientes.get(0);
					if(cliente != null) {
						session.setAttribute("cliente", cliente.getId());
					}
				}
			}
		}
		
		if(cliente == null) {
			session.setAttribute("cliente", 0);
			model.addAttribute("cliente",new Cliente());
			model.addAttribute("msg", "No se encontro el cliente");
			model.addAttribute("carpeta",carpetaTemp);
			return "prestamos/form :: #buscadorAgregarPrestamo"; 
		}

		model.addAttribute("carpeta",carpetaTemp);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
	}
	
	@PostMapping("/calculoCuota")
	public String calculoCuota(Model model, @ModelAttribute("prestamo") Prestamo prestamo, @RequestParam("fecha") String fecha) {
		int fre = 0;
		Double totalPagar  = 0.0;
		Double interes = prestamo.getTasa();
		Double cuota = 0.0;
		Double capital = 0.0;
		Double total_pagar = 0.0;
		
		Date date;
		try {
			date = formatter.parse(fecha);
			prestamo.setFecha(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Forma de pago
		if (prestamo.getForma_pago().equals("1")) {
			fre = 30;
		} else if (prestamo.getForma_pago().equals("2")) {
			fre = 15;
		} else if (prestamo.getForma_pago().equals("7")) {
			fre = 4;
		} else if (prestamo.getForma_pago().equals("15")) {
			fre = 2;
		} else if (prestamo.getForma_pago().equals("30")) {
			fre = 1;
		}
		
		//Tipos
		if(prestamo.getTipo().equals("1")) {
			//Coutas fijas
			interes = (prestamo.getMonto()*(prestamo.getTasa()/100)*(prestamo.getPagos().doubleValue()/fre))/prestamo.getPagos();

		    capital = prestamo.getMonto() / prestamo.getPagos();
		    cuota =   capital + interes;
		    total_pagar = cuota * prestamo.getPagos();
		
		}else if(prestamo.getTipo().equals("3")) {
			//Cuotas variables	
			  Double cant_prestada = prestamo.getMonto();
			  cuota = cant_prestada * (Math.pow((1+(prestamo.getTasa()/100)), prestamo.getPagos()) * (prestamo.getTasa()/100)) / (Math.pow((1+(prestamo.getTasa()/100)), prestamo.getPagos()) - 1);

			  Double pagoInteres = cant_prestada * (prestamo.getTasa()/100);
			  Double pagoCapital = cuota - pagoInteres;
			  cant_prestada-= pagoCapital;

			  interes=cuota; //pagoInteres -> Segun validaciones del front toma el mismo valor de la cuota
			  capital=pagoCapital;	
			  total_pagar = cuota * prestamo.getPagos();
		}
		
		model.addAttribute("capital", capital > 0 ? formato2d(capital) : capital);
		model.addAttribute("cuota", cuota > 0 ? formato2d(cuota) : cuota);
		model.addAttribute("interes", interes > 0 ? formato2d(interes) : interes);
		model.addAttribute("total_pagar", total_pagar > 0 ? formato2d(total_pagar) : total_pagar);
		return "prestamos/form :: #calculosCuota";
	}
	
	@PostMapping("/calculoTasa")
	public String calculoTasa(Model model, @ModelAttribute("prestamo") Prestamo prestamo, @RequestParam("fecha") String fecha) {
		int fre = 0;
		Double totalPagar  = 0.0;
		Double interes = prestamo.getTasa();
		Double cuota = prestamo.getValor_cuota();
		Double capital = 0.0;
		Double total_pagar = 0.0;
		
		Date date;
		try {
			date = formatter.parse(fecha);
			prestamo.setFecha(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Forma de pago
		if (prestamo.getForma_pago().equals("1")) {
			fre = 30;
		} else if (prestamo.getForma_pago().equals("2")) {
			fre = 15;
		} else if (prestamo.getForma_pago().equals("7")) {
			fre = 4;
		} else if (prestamo.getForma_pago().equals("15")) {
			fre = 2;
		} else if (prestamo.getForma_pago().equals("30")) {
			fre = 1;
		}
		
		capital = prestamo.getMonto() / prestamo.getPagos();
		interes = cuota - capital;
		total_pagar = cuota * prestamo.getPagos();
		
		Double f= (double) (prestamo.getPagos()/fre);
		
		Double tasa = ((100) * interes * prestamo.getPagos()) / (prestamo.getMonto() *(prestamo.getPagos().doubleValue()/fre)) ;
		
		cuota = capital + interes;

		
		model.addAttribute("capital", formato2d(capital));
		model.addAttribute("tasa", formato2d(tasa));
		model.addAttribute("interes", formato2d(interes));
		model.addAttribute("total_pagar", formato2d(total_pagar));
		return "prestamos/form :: #calculosTasa";
	}
	
	@PostMapping("/amortizar")
	public String amortizar(Model model, @ModelAttribute("prestamo") Prestamo prestamo) throws ParseException {
		int fre = 0;
		double total_cuota = 0;
		double total_capital = 0;
		double total_interes = 0;
		double total_neto = 0;
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Amortizacion> detalles = new LinkedList<>();

		if (prestamo.getPagos() != 0) {

			// Forma de pago
			if (prestamo.getForma_pago().equals("1")) {
				fre = 30;
			} else if (prestamo.getForma_pago().equals("2")) {
				fre = 15;
			} else if (prestamo.getForma_pago().equals("7")) {
				fre = 4;
			} else if (prestamo.getForma_pago().equals("15")) {
				fre = 2;
			} else if (prestamo.getForma_pago().equals("30")) {
				fre = 1;
			}
			
			Date date = formato.parse(prestamo.getFechaTemp());
			LocalDate fecha_pagoTemp = date.toInstant().atZone(ZoneId.systemDefault().systemDefault())
					.toLocalDate();
			int mes = fecha_pagoTemp.getMonthValue();
			int agno = fecha_pagoTemp.getYear();
			String fecha_pago = fecha_pagoTemp.getDayOfMonth() + "-" + mes + "-" + agno;

			if (prestamo.getTipo().equals("1")) {
				//Coutas Fijas
				int x = 0;

				double interes = (prestamo.getMonto() * (prestamo.getTasa() / 100) * (prestamo.getPagos().doubleValue() / fre))
						/ prestamo.getPagos();

				double capital = prestamo.getMonto() / prestamo.getPagos();
				double cuota = capital + interes;
				double total_pagar = cuota * prestamo.getPagos();

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
					total_pagar = total_pagar - (cuota);

					if (prestamo.getForma_pago().equals("30")) {
						// *************************************************** */

						int dia = fecha_pagoTemp.getDayOfMonth();

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

						// **************************************************** */
					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}

//					System.out.println(cuota + " - " + capital + " - " + interes + " " + total_pagar);
					x++;
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));
					amortizacion.setNumero(x);
					detalles.add(amortizacion);
				}

			} else if (prestamo.getTipo().equals("3")) {
				// Cuotas variables
				double interes = prestamo.getTasa().doubleValue();
				double cuota = prestamo.getMonto()
						* (Math.pow((1 + (interes / 100)), prestamo.getPagos()) * (interes / 100))
						/ (Math.pow((1 + (interes / 100)), prestamo.getPagos()) - 1);
				
				double capital = 0;
				double total_pagar = 0;

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					if (prestamo.getForma_pago().equals("30")) {

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						int dia = fecha_pagoTemp.getDayOfMonth();

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}
					
					if(i>1) {
						double tempPorcent = prestamo.getTasa() / 100;
						tempPorcent = tempPorcent * capital;
						capital += tempPorcent;
						interes = cuota - capital;
						total_pagar = ((total_pagar - interes) - capital) + interes;

					}else {
						interes = prestamo.getMonto() * (interes / 100);
						capital = cuota - interes;
						total_pagar = prestamo.getMonto() - capital;
					}

					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));

					detalles.add(amortizacion);

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
				}
			}
		}else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			Calendar calendar = Calendar.getInstance();
			Amortizacion amortizacion = new Amortizacion();
			amortizacion.setFecha(sdf.format(calendar.getTime()));
			amortizacion.setCuota(0.00);
			amortizacion.setCapital(0.00);
			amortizacion.setInteres(0.00);
			amortizacion.setSaldo(0.00);
			detalles.add(amortizacion);
		}

		model.addAttribute("detalles", detalles);
		model.addAttribute("totalCuota", total_cuota >0 ? formato2d(total_cuota):total_cuota);
		model.addAttribute("totalCapital", total_capital >0 ? formato2d(total_capital) : total_capital);
		model.addAttribute("totalInteres", total_interes >0 ? formato2d(total_interes) : total_interes);
		model.addAttribute("totalNeto", total_neto >0 ? formato2d(total_neto) : total_neto);
		return "index :: #cuerpo_amortizacion";
	}
	
	@PostMapping("/guardar")
	@ResponseBody
	public ResponseEntity<String> guardar(HttpSession session, Model model, @ModelAttribute("prestamo") Prestamo prestamo) throws ParseException {
		int fre = 0;
		double total_cuota = 0;
		double total_capital = 0;
		double total_interes = 0;
		double total_neto = 0;
		Integer response = 0;
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
				
		List<Amortizacion> detalles = new LinkedList<>();
		

		if (prestamo.getPagos() != 0) {

			// Forma de pago
			if (prestamo.getForma_pago().equals("1")) {
				fre = 30;
			} else if (prestamo.getForma_pago().equals("2")) {
				fre = 15;
			} else if (prestamo.getForma_pago().equals("7")) {
				fre = 4;
			} else if (prestamo.getForma_pago().equals("15")) {
				fre = 2;
			} else if (prestamo.getForma_pago().equals("30")) {
				fre = 1;
			}
			
			Date date = formato.parse(prestamo.getFechaTemp());
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDate fecha_pagoTemp = date.toInstant().atZone(zoneId)
					.toLocalDate();
			int mes = fecha_pagoTemp.getMonthValue();
			int agno = fecha_pagoTemp.getYear();
			String fecha_pago = fecha_pagoTemp.getDayOfMonth() + "-" + mes + "-" + agno;

			if (prestamo.getTipo().equals("1")) {
				//Coutas Fijas
				int x = 0;

				double interes = (prestamo.getMonto() * (prestamo.getTasa() / 100) * (prestamo.getPagos().doubleValue() / fre))
						/ prestamo.getPagos();

				double capital = prestamo.getMonto() / prestamo.getPagos();
				double cuota = capital + interes;
				double total_pagar = cuota * prestamo.getPagos();

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
					total_pagar = total_pagar - (cuota);

					if (prestamo.getForma_pago().equals("30")) {
						// *************************************************** */

						int dia = fecha_pagoTemp.getDayOfMonth();

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

						// **************************************************** */
					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}

//					System.out.println(cuota + " - " + capital + " - " + interes + " " + total_pagar);
					x++;
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));
					amortizacion.setNumero(x);
					detalles.add(amortizacion);
				}

			} else if (prestamo.getTipo().equals("3")) {
				// Cuotas variables
				double interes = prestamo.getTasa().doubleValue();
				double cuota = prestamo.getMonto()
						* (Math.pow((1 + (interes / 100)), prestamo.getPagos()) * (interes / 100))
						/ (Math.pow((1 + (interes / 100)), prestamo.getPagos()) - 1);
				
				double capital = 0;
				double total_pagar = 0;

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					if (prestamo.getForma_pago().equals("30")) {

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						int dia = fecha_pagoTemp.getDayOfMonth();

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}
					
					if(i>1) {
						double tempPorcent = prestamo.getTasa() / 100;
						tempPorcent = tempPorcent * capital;
						capital += tempPorcent;
						interes = cuota - capital;
						total_pagar = ((total_pagar - interes) - capital) + interes;

					}else {
						interes = prestamo.getMonto() * (interes / 100);
						capital = cuota - interes;
						total_pagar = prestamo.getMonto() - capital;
					}

					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));

					detalles.add(amortizacion);

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
				}
			}
		}else {
			if(prestamo.getTipo().equals("2")) {
				//interes
				total_neto = prestamo.getMonto();
			}else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
				Calendar calendar = Calendar.getInstance();
				Amortizacion amortizacion = new Amortizacion();
				amortizacion.setFecha(sdf.format(calendar.getTime()));
				amortizacion.setCuota(0.00);
				amortizacion.setCapital(0.00);
				amortizacion.setInteres(0.00);
				amortizacion.setSaldo(0.00);
				detalles.add(amortizacion);
			}
		}
		
		//Guardamos el prestamo
		Cliente cliente = serviceClientes.buscarPorId(prestamo.getIdClienteTemp());
		Carpeta carpeta = serviceCarpetas.buscarPorId(prestamo.getIdCarpetaTemp());
		
		int codigo = 1;
		
		//Buscamos la lista de prestamos asociados a la carpeta
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpeta(carpeta);
		if(!prestamos.isEmpty()) {
			Prestamo tempPrestamo = prestamos.get(prestamos.size()-1);
			codigo = tempPrestamo.getCodigo()+1;
		}
		
		if(prestamo.getIdCuentaTemp()!=null) {
			Cuenta cuenta = serviceCuentas.buscarPorId(prestamo.getIdCuentaTemp());
			prestamo.setCuenta(cuenta);
			if((cuenta.getMonto()-prestamo.getMonto()) < 0) {
				response = 0;
//				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}
		}else {
			prestamo.setCuenta(null);
		}
		
		double capital = total_capital >0 ? formato2d(total_capital) : total_capital;
				
		prestamo.setCliente(cliente);
		prestamo.setCarpeta(carpeta);
		prestamo.setUsuario(usuario);
		prestamo.setBalance(total_neto >0 ? formato2d(total_neto) : total_neto);
//		prestamo.setEstado(estado); //verificar
		prestamo.setCodigo(codigo);
		prestamo.setFecha(formato.parse(prestamo.getFechaTemp()));
		
		LocalDateTime fechaCronTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha()).plusDays(prestamo.getDias_gracia());
		Date fechaCron = convertToDateViaInstant(fechaCronTemp);
		
		prestamo.setEstado(0);
		prestamo.setFecha_cron(fechaCron);
		prestamo.setTotal_cuota(total_cuota >0 ? formato2d(total_cuota):total_cuota);
		prestamo.setTotal_capital(!prestamo.getTipo().equals("2")?capital:prestamo.getMonto());
		prestamo.setTotal_interes(total_interes >0 ? formato2d(total_interes) : total_interes);
		prestamo.setTotal_neto(total_neto >0 ? formato2d(total_neto) : total_neto);
		servicePrestamos.guardar(prestamo);
		
		//Guardamos los detalles de la amortizacion en el prestamo
		if(!prestamo.getTipo().equals("2")) {
			for (Amortizacion amortizacion : detalles) {
				PrestamoDetalle prestamoDetalle = new PrestamoDetalle();
				prestamoDetalle.setPrestamo(prestamo);
				prestamoDetalle.setBalance(amortizacion.getSaldo());
				prestamoDetalle.setCapital(amortizacion.getCapital());
				prestamoDetalle.setNumero(amortizacion.getNumero());
				prestamoDetalle.setCuota(amortizacion.getCuota());
				prestamoDetalle.setFecha(new Date());
				prestamoDetalle.setFechaGenerada(formato2.parse(amortizacion.getFecha()));
				prestamoDetalle.setEstado_cuota("Normal");
//				prestamoDetalle.setFechaInteres(new Date());
				prestamoDetalle.setGenerarInteres(amortizacion.getInteres()>0?1:0);
				prestamoDetalle.setInteres(amortizacion.getInteres());
				prestamoDetalle.setInteres_mora(prestamo.getMora());
//				prestamoDetalle.setMonto(amortizacion.getCuota());
//				prestamoDetalle.setMora(amortizacion.getm);
				prestamoDetalle.setPago(0);
				servicePrestamosDetalles.guardar(prestamoDetalle);
			}
			
			if(prestamo.getCantidad_pagos() > 0) {
				double adicionales = prestamo.getGastos_cierre() / prestamo.getCantidad_pagos();
				List<PrestamoDetalle> prestamosDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
								
				for (int i = 0; i < prestamo.getCantidad_pagos(); i++) {
					PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
					prestamoAdicional.setMonto(adicionales);
					prestamoAdicional.setFecha(new Date());
					prestamoAdicional.setMotivo("Gastos Cierre");
					prestamoAdicional.setPrestamo(prestamo);
					prestamoAdicional.setPrestamoDetalle(prestamosDetalles.get(i));
					prestamoAdicional.setUsuario(usuario);
					servicePrestamosAdicionales.guardar(prestamoAdicional);
				}
			}
		}else {
			//Interes
			if(prestamo.getCantidad_pagos() > 0) {
				double adicionales = prestamo.getGastos_cierre() / prestamo.getCantidad_pagos();
				for (int i = 0; i < prestamo.getCantidad_pagos(); i++) {
					LocalDateTime fechaAcct =  LocalDateTime.now();
					LocalDateTime fechaVenciminto = fechaAcct.plusMonths(1);
					Date vencimiento = convertToDateViaInstant(fechaVenciminto);
					PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
					prestamoAdicional.setMonto(adicionales);
					prestamoAdicional.setFecha(prestamo.getFecha());
					prestamoAdicional.setMotivo("Gastos Cierre");
					prestamoAdicional.setPrestamo(prestamo);
					prestamoAdicional.setUsuario(usuario);
					prestamoAdicional.setNumeroCuota(i+1);
					prestamoAdicional.setFecha_vencimiento(vencimiento);
					servicePrestamosAdicionales.guardar(prestamoAdicional);
				}
			}
		}
		
		if(prestamo.getId()!=null) {
			if(prestamo.getCuenta()!=null) {
				Cuenta cuenta = prestamo.getCuenta();
				cuenta.setMonto(cuenta.getMonto()-prestamo.getMonto());
				serviceCuentas.guardar(cuenta);
			}
			response = 1;
		}
		
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/detalleAmortizacion/{id}")
	public String detalleAmortizacion(Model model, @PathVariable(name = "id") Integer id, HttpSession session) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		List<Amortizacion> detalles = new LinkedList<>();
		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
		double totalPendiente = 0;
		double sumaInteresGen = 0;
		double sumaMora = 0;
		double sumaCargo = 0;
		double sumaAbono = 0;
		double sumaBalance = 0;
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			int count = 0;
			double mora = 0;
			double interes = 0;
			double capital = 0;
			if(!prestamoInteresDetalles.isEmpty()) {
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
					count++;
					String estado = "Normal";
					if(prestamoInteresDetalle.getEstado()==2) {
						estado = "Atraso";
					}else if(prestamoInteresDetalle.getEstado()==1) {
						estado = "Saldo";
					}
					
					double cargoPagadoTemp = 0;
					
					Amortizacion amortizacion = new Amortizacion();
					
					double descuentoAdicionales = 0;
					double descuentoCargo = 0;
					
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());
					if(!adicionales.isEmpty()) {
						double cargo = 0;
						for (PrestamoAdicional adicionalTemp : adicionales) {
							cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
							cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
							descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
						}
						amortizacion.setCargo(cargo);
						descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
						amortizacion.setCargo(cargo-descuentoAdicionales);
					}
					
					LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha_cuota()).minusMonths(1);
					LocalDateTime dateAcct = LocalDateTime.now();
					int vencidos = (int)diasVencidos(fechaTemp, dateAcct);
					double interesXHoy = prestamoInteresDetalle.getInteres()/30.00*vencidos;
					
					if(interesXHoy>=prestamoInteresDetalle.getInteres()) {
						interesXHoy = prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado();
					}
					
					double balance = (prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado())+(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora())+(amortizacion.getCargo()==null?0:descuentoCargo);
					double abono = prestamoInteresDetalle.getInteres_pagado()+prestamoInteresDetalle.getMora_pagada()+cargoPagadoTemp;
					
					amortizacion.setBalance(formato2d(balance));
					amortizacion.setAbono(formato2d(abono));
					amortizacion.setInteresXhoy(balance==0?0:formato2d(interesXHoy));
					amortizacion.setNumero(count);
					amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
					capital = prestamoInteresDetalle.getCapital();
					capital-= prestamoInteresDetalle.getCapital_pagado();
					amortizacion.setInteres(formato2d(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado()));
					amortizacion.setTipo(2);
					
					mora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora();

					amortizacion.setMora(formato2d(mora));
					amortizacion.setEstado(estado);
					amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
					
					double descuentoAdicional = 0;
					
					List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
							.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota());
					for (PrestamoAdicional adicional : adicionalTemp) {
						descuentoAdicional += adicional.getDescuento_adicionales();
					}

					amortizacion.setDescuento(prestamoInteresDetalle.getDescuento_mora()+descuentoAdicional);
					
					amortizacion.setId(prestamoInteresDetalle.getId());
					detalles.add(amortizacion);
					mora+=prestamoInteresDetalle.getMora();
					interes+=prestamoInteresDetalle.getInteres();
					
					sumaInteresGen+= amortizacion.getInteres();
					sumaMora += amortizacion.getMora();
					sumaAbono += amortizacion.getAbono();
					sumaBalance += amortizacion.getBalance();
										
				}
				detalles.get(0).setCuota(formato2d(mora+interes));
				detalles.get(0).setCapital(formato2d(capital));
			}
			
			List<PrestamoAdicional> pretamoAdicionalTemp = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
			for (PrestamoAdicional prestamoAdicional : pretamoAdicionalTemp) {
				sumaCargo+=prestamoAdicional.getMonto()-prestamoAdicional.getMonto_pagado()-prestamoAdicional.getDescuento_adicionales();
			}
			
		}else {
			//Cuotas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
			detalles = new LinkedList<>();
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				Amortizacion amortizacion = new Amortizacion();
				amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
				amortizacion.setCuota(prestamoDetalle.getCuota());
				amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
				amortizacion.setCapital(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado());
				amortizacion.setInteres(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado());
				amortizacion.setSaldo(prestamoDetalle.getBalance());
				amortizacion.setNumero(prestamoDetalle.getNumero());
				amortizacion.setMora(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada());
				amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
				amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
				detalles.add(amortizacion);
			}
		}
		
		Integer idCliente = (Integer) session.getAttribute("cliente");
		Cliente cliente = serviceClientes.buscarPorId(idCliente);
		String datosCliente  = cliente.getNombre()+" / Tel.: "+cliente.getTelefono();
		
		model.addAttribute("detalles", detalles);
		model.addAttribute("totalCuota", prestamo.getTotal_cuota());
		model.addAttribute("totalCapital", prestamo.getTotal_capital());
		model.addAttribute("totalInteres", prestamo.getTotal_interes());
		model.addAttribute("totalNeto", prestamo.getTotal_neto());
		model.addAttribute("numeroPrestamo", "No. Prestamo: "+prestamo.getCodigo());
		model.addAttribute("montoPrestamo", "Prestado: "+prestamo.getMonto());
		model.addAttribute("capitalPendiente", "Capital Pendiente: "+prestamo.getBalance());
		model.addAttribute("totalPendiente", "Total Pendiente: "+totalPendiente);
		model.addAttribute("datosCliente", datosCliente);
		
		model.addAttribute("sumaInteresGen", formato2d(sumaInteresGen));
		model.addAttribute("sumaMora", formato2d(sumaMora));
		model.addAttribute("sumaCargo", formato2d(sumaCargo));
		model.addAttribute("sumaAbono", formato2d(sumaAbono));
		model.addAttribute("sumaBalance", formato2d(sumaBalance));

		model.addAttribute("capitalPendienteTemp", formato2d(prestamo.getBalance()));
		return "index :: #cuerpo_amortizacion";
	}
	
	@GetMapping("/detallesCargos/{id}")
	public String detallesCargos(Model model, @PathVariable("id") Integer id) {
		PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(id);
		List<PrestamoAdicional> adicionales = new LinkedList<>();
		if(prestamoInteresDetalle != null) {
			adicionales = servicePrestamosAdicionales.
					buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());

			if(adicionales.isEmpty()) {
				adicionales = servicePrestamosAdicionales.
						buscarPorPrestamo(prestamoInteresDetalle.getPrestamo());
			}
		}
		model.addAttribute("cargos", adicionales);
		return "index :: #tablaCargos";
	}
	
	@PostMapping("/totalesCuotas/")
	public String totalesCuotas(Model model, HttpSession session,
			Integer idPrestamo, Integer tipoCuota, String cuotas) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		List<Amortizacion> detalles = new LinkedList<>();
		cuotas = cuotas.replace(" ", "");
		cuotas = cuotas.substring(0, cuotas.length()-1);
		String[] cuotasArray = cuotas.split(",");
		double capital = 0;
		double sumaMora = 0;
		double cargo = 0;
		double sumaAbono = 0;
		double sumaBalance = 0;
		double sumaDescuento = 0;
		double sumaCargo = 0;
		
		if(tipoCuota == 1) {
			//Interes
			if(cuotasArray.length>0) {
				for (String id : cuotasArray) {
					PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(Integer.parseInt(id));
					
						String estado = "Normal";
						if(prestamoInteresDetalle.getEstado()==2) {
							estado = "Atraso";
						}else if(prestamoInteresDetalle.getEstado()==1) {
							estado = "Saldo";
						}
						
						double cargoPagadoTemp = 0;
						
						Amortizacion amortizacion = new Amortizacion();
						
						double descuentoAdicionales = 0;
						double descuentoCargo = 0;
						
						List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());
						if(!adicionales.isEmpty()) {
							double cargoAdicional = 0;
							for (PrestamoAdicional adicionalTemp : adicionales) {
								cargoAdicional+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
								cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
								descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
							}
							amortizacion.setCargo(cargoAdicional);
							descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
							amortizacion.setCargo(cargoAdicional-descuentoAdicionales);
						}
						
						LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha_cuota()).minusMonths(1);
						LocalDateTime dateAcct = LocalDateTime.now();
						int vencidos = (int)diasVencidos(fechaTemp, dateAcct);
						double interesXHoy = prestamoInteresDetalle.getInteres()/30.00*vencidos;
						
						if(interesXHoy>=prestamoInteresDetalle.getInteres()) {
							interesXHoy = prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado();
						}
						
						double balance = (prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado())+(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora())+(amortizacion.getCargo()==null?0:descuentoCargo);
						double abono = prestamoInteresDetalle.getInteres_pagado()+prestamoInteresDetalle.getMora_pagada()+cargoPagadoTemp;
						
						amortizacion.setBalance(formato2d(balance));
						amortizacion.setAbono(formato2d(abono));
						amortizacion.setInteresXhoy(balance==0?0:formato2d(interesXHoy));
						amortizacion.setNumero(prestamoInteresDetalle.getNumero_cuota());
						amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
						capital = prestamoInteresDetalle.getCapital();
						capital-= prestamoInteresDetalle.getCapital_pagado();
						amortizacion.setInteres(formato2d(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado()));
						amortizacion.setTipo(2);
						
						double mora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora();

						amortizacion.setMora(formato2d(mora));
						amortizacion.setEstado(estado);
						amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
						
						double descuentoAdicional = 0;
						
						List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota());
						for (PrestamoAdicional adicional : adicionalTemp) {
							descuentoAdicional += adicional.getDescuento_adicionales();
						}

						amortizacion.setDescuento(prestamoInteresDetalle.getDescuento_mora()+descuentoAdicional);
						
						amortizacion.setId(prestamoInteresDetalle.getId());
						detalles.add(amortizacion);
						mora+=prestamoInteresDetalle.getMora();
						Double interes = prestamoInteresDetalle.getInteres();
						
//						Double sumaInteresGen = amortizacion.getInteres();
						sumaDescuento += amortizacion.getDescuento();
						sumaMora += amortizacion.getMora();
						sumaAbono += amortizacion.getAbono();
						sumaBalance += amortizacion.getBalance();
						sumaCargo += amortizacion.getCargo();
											
					detalles.get(0).setCuota(formato2d(mora+interes));
					detalles.get(0).setCapital(formato2d(capital));
				}
			}
		}else {
			//Cuota
		}
		
		model.addAttribute("totalDescuento", sumaDescuento);
		model.addAttribute("totalMora", sumaMora);
		model.addAttribute("totalCargo", sumaCargo);
		model.addAttribute("totalBalance", sumaBalance);
		
		return "clientes/infoCliente :: #totalesGeneralesPrestamo";
	}
	
	@GetMapping("/detallesMoras/{id}")
	public String detallesMoras(Model model, @PathVariable("id") Integer id) {
		PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(id);
		model.addAttribute("detalles", prestamoInteresDetalle);
		return "index :: #tablaMoras";
	}

	@GetMapping("/cuotasNoPagadas/{id}")
	public String cargarCuotasNoPagadas(Model model, @PathVariable("id") Integer id) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		
		List<PrestamoDetalleMixto> prestamosDetallesMixto = new LinkedList<>();
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			List<PrestamoInteresDetalle> prestamoDetallesInteres = servicePrestamosInteresesDetalles.buscarPorPrestamoEstadoPago(prestamo, 0);
			if(!prestamoDetallesInteres.isEmpty()) {
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoDetallesInteres) {
					if(prestamoInteresDetalle.getEstadoPago().doubleValue()==0) {
						PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
						prestamoDetalleMixto.setId(prestamoInteresDetalle.getId());
						prestamoDetalleMixto.setNumero(prestamoInteresDetalle.getNumero_cuota());
						prestamoDetalleMixto.setFecha(prestamoInteresDetalle.getFecha_cuota());
						prestamosDetallesMixto.add(prestamoDetalleMixto);
					}else {
						PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
						
						List<PrestamoInteresDetalle> prestamoDetallesInteresTemp = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
						if(!prestamoDetallesInteresTemp.isEmpty()) {
							PrestamoInteresDetalle prestamoDetalleTemp = prestamoDetallesInteresTemp.get(prestamoDetallesInteresTemp.size()-1);
							Integer numCuotaTemp = prestamoDetalleTemp.getNumero_cuota()+1;
							prestamoDetalleMixto.setId(numCuotaTemp);
							prestamoDetalleMixto.setNumero(numCuotaTemp);
							prestamoDetalleMixto.setFecha(prestamoInteresDetalle.getFecha_cuota());
						}else {
							prestamoDetalleMixto.setId(1);
							prestamoDetalleMixto.setNumero(1);
							prestamoDetalleMixto.setFecha(prestamoInteresDetalle.getFecha_cuota());
						}
						prestamosDetallesMixto.add(prestamoDetalleMixto);
						break;
					}
				}
			}else {
				List<PrestamoInteresDetalle> prestamoDetallesInteresTemp = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
				if(prestamoDetallesInteresTemp.isEmpty()) {
					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
					prestamoDetalleMixto.setId(1);
					prestamoDetalleMixto.setNumero(1);
					prestamoDetalleMixto.setFecha(new Date());
					prestamosDetallesMixto.add(prestamoDetalleMixto);
				}else {
					PrestamoInteresDetalle detalleTemp = prestamoDetallesInteresTemp.get(prestamoDetallesInteresTemp.size()-1);
					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
					prestamoDetalleMixto.setId(detalleTemp.getNumero_cuota()+1);
					prestamoDetalleMixto.setNumero(detalleTemp.getNumero_cuota()+1);
					prestamoDetalleMixto.setFecha(new Date());
					prestamosDetallesMixto.add(prestamoDetalleMixto);
				}
			}
		}else {
			//cuotas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getPago() == 0).collect(Collectors.toList());
			if(!prestamoDetalles.isEmpty()) {
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
					prestamoDetalleMixto.setId(prestamoDetalle.getId());
					prestamoDetalleMixto.setNumero(prestamoDetalle.getNumero());
					prestamoDetalleMixto.setFecha(prestamoDetalle.getFechaGenerada());
					prestamosDetallesMixto.add(prestamoDetalleMixto);
				}
			}
		}
		model.addAttribute("prestamoDetalles", prestamosDetallesMixto);
		return "clientes/infoCliente :: #selectCuotaCargo";
	}
	
	@GetMapping("/cuotasNoPagadasDescuentos/{id}/{tipo}")
	public String cargarCuotasNoPagadasDescuentos(Model model, @PathVariable("id") Integer id,
			@PathVariable("tipo") Integer tipo) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		List<PrestamoDetalleMixto> prestamosDetallesMixto = new LinkedList<>();
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			
			List<PrestamoInteresDetalle> prestamoDetallesInteres = new LinkedList<>();
			
			if(tipo == 1) {
				prestamoDetallesInteres = servicePrestamosInteresesDetalles.
						buscarPorPrestamo(prestamo).stream().filter(p -> p.getPagado().doubleValue() == 0
								&& p.getMora()-p.getMora_pagada() > 0).collect(Collectors.toList());
			}else if(tipo == 2) {
				prestamoDetallesInteres = servicePrestamosInteresesDetalles.buscarPorPrestamoEstadoPago(prestamo, 0);
			}

			if(!prestamoDetallesInteres.isEmpty()) {
				
				if(tipo == 1) {
					for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoDetallesInteres) {
						if(prestamoInteresDetalle.getEstadoPago().doubleValue()==0) {
							PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
							prestamoDetalleMixto.setId(prestamoInteresDetalle.getId());
							prestamoDetalleMixto.setNumero(prestamoInteresDetalle.getNumero_cuota());
							prestamoDetalleMixto.setFecha(prestamoInteresDetalle.getFecha_cuota());
							prestamosDetallesMixto.add(prestamoDetalleMixto);
							prestamoDetalleMixto.setItem("Cuota "+prestamoInteresDetalle.getNumero_cuota()+" - "+sdf.format(prestamoInteresDetalle.getFecha_cuota()));
						}
					}
				}else if(tipo == 2) {
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
					for (PrestamoAdicional prestamoAdicional : adicionales) {
						PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
						prestamoDetalleMixto.setId(prestamoAdicional.getId());
						prestamoDetalleMixto.setNumero(prestamoAdicional.getNumeroCuota());
						prestamoDetalleMixto.setFecha(prestamoAdicional.getFecha());
						prestamoDetalleMixto.setItem(prestamoAdicional.getNumeroCuota()+" - "+prestamoAdicional.getMotivo());
						prestamosDetallesMixto.add(prestamoDetalleMixto);
					}
				}
			}else {
				//condicion
			}
		}else {
			//cuotas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getPago() == 0).collect(Collectors.toList());
			if(!prestamoDetalles.isEmpty()) {
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
					prestamoDetalleMixto.setId(prestamoDetalle.getId());
					prestamoDetalleMixto.setNumero(prestamoDetalle.getNumero());
					prestamoDetalleMixto.setFecha(prestamoDetalle.getFechaGenerada());
					prestamosDetallesMixto.add(prestamoDetalleMixto);
				}
			}
		}
		model.addAttribute("prestamoDetalles", prestamosDetallesMixto);
		return "clientes/infoCliente :: #selectCuotasMora";
	}
	
	@PostMapping("/guardarDescuento/")
	@ResponseBody
	public ResponseEntity<String> guardarDescuento(Model model, HttpSession session,
			Integer idPrestamo, Double monto, Integer tipo, Integer cuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Integer response = 0;
		
		if(tipo == 1) {
			//Mora
			PrestamoInteresDetalle prestamoInteresDetalleTemp = servicePrestamosInteresesDetalles.buscarPorId(cuota);
			List<PrestamoInteresDetalle> detalles = servicePrestamosInteresesDetalles.
					buscarPorPrestamo(prestamo).stream().filter(d -> d.getNumero_cuota() == prestamoInteresDetalleTemp.getNumero_cuota()).
					collect(Collectors.toList());
			if(!detalles.isEmpty()) {
				PrestamoInteresDetalle prestamoInteresDetalle = detalles.get(0);
				prestamoInteresDetalle.setDescuento_mora(prestamoInteresDetalle.getDescuento_mora() + monto);
				servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
				response = 1;
			}
		}else if(tipo == 2) {
			//Adicionales
			PrestamoAdicional adicional = servicePrestamosAdicionales.buscarPorId(cuota);
			adicional.setDescuento_adicionales(adicional.getDescuento_adicionales() + monto);
			servicePrestamosAdicionales.guardar(adicional);
			response = 1;
			
			if(formato2d(adicional.getMonto().doubleValue()) == formato2d(adicional.getDescuento_adicionales()+adicional.getMonto_pagado())) {
				adicional.setEstado(1);
				servicePrestamosAdicionales.guardar(adicional);
			}
		}

		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/validarDescuento/")
	@ResponseBody
	public ResponseEntity<String> validarDescuento(Model model, HttpSession session,
			Integer idPrestamo, Double monto, Integer tipo, Integer cuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Integer response = 0;
		
		if(tipo == 1) {
			//Mora
			PrestamoInteresDetalle prestamoInteresDetalleTemp = servicePrestamosInteresesDetalles.buscarPorId(cuota);
			List<PrestamoInteresDetalle> detalles = servicePrestamosInteresesDetalles.
					buscarPorPrestamo(prestamo).stream().filter(d -> d.getNumero_cuota() == prestamoInteresDetalleTemp.getNumero_cuota()).
					collect(Collectors.toList());
			if(!detalles.isEmpty()) {
				double montoTemp = detalles.get(0).getMora()-detalles.get(0).getMora_pagada()-detalles.get(0).getDescuento_mora();
				if(montoTemp >= monto) {
					response = 1;
				}
			}
		}else if(tipo == 2) {
			//Adicionales
			PrestamoAdicional adicional = servicePrestamosAdicionales.buscarPorId(cuota);
			double montoTemp = adicional.getMonto()-adicional.getMonto_pagado()-adicional.getDescuento_adicionales();
			if(montoTemp >= monto) {
				response = 1;
			}
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/guardarCargoCuota/")
	@ResponseBody
	public ResponseEntity<String> guardarCargoCuota(Model model, HttpSession session,
			Integer idPrestamo, String motivo, String nota,
			Double monto,  Integer cuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
		PrestamoDetalle prestamoDetalle = null;
		Integer response = 0;

		prestamoAdicional.setFecha(new Date());
	
		if(prestamo.getTipo().equals("2")) {
			//Interes
			prestamoAdicional.setFecha_vencimiento(new Date());
//			cuota
			if(cuota>0) {
				PrestamoInteresDetalle adicionalTemp = servicePrestamosInteresesDetalles.buscarPorId(cuota);
				if(adicionalTemp!=null) {
					prestamoAdicional.setNumeroCuota(adicionalTemp.getNumero_cuota());
				}else {
					prestamoAdicional.setNumeroCuota(cuota);
				}
			}
		}else {
			//cuotas
			prestamoDetalle = servicePrestamosDetalles.buscarPorId(cuota);
			prestamoAdicional.setPrestamoDetalle(prestamoDetalle);
			prestamoAdicional.setFecha_vencimiento(prestamoDetalle.getFechaGenerada());
		}
		
		prestamoAdicional.setNota(nota);
		prestamoAdicional.setMonto(monto);
		prestamoAdicional.setMotivo(motivo);
		prestamoAdicional.setPrestamo(prestamo);
		prestamoAdicional.setUsuario(usuario);
		servicePrestamosAdicionales.guardar(prestamoAdicional);
		if(prestamoAdicional.getId()!=null) {
			response = 1;
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/infoAbonos/")
	@ResponseBody
	public ResponseEntity<String> infoAbonos(Model model, HttpSession session, Integer idPrestamo) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		if(prestamo.getTipo().equals("2")) {
			//Interes
			double moraPagada = 0;
			double interesPagado = 0;
			double capitalPagado = 0;
			double cargosPagado = 0;
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			List<PrestamoAdicional> prestamoAdicionales = servicePrestamosAdicionales.buscarPorPrestamo(prestamo);
			if(!prestamoInteresDetalles.isEmpty()) {
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
					moraPagada+=prestamoInteresDetalle.getMora_pagada();
					interesPagado+=prestamoInteresDetalle.getInteres_pagado();
					capitalPagado+=prestamoInteresDetalle.getCapital_pagado();
				}
			}
			if(!prestamoAdicionales.isEmpty()) {
				for (PrestamoAdicional prestamoAdicional : prestamoAdicionales) {
					cargosPagado+=prestamoAdicional.getMonto();
				}
			}
		}else {
			//cuotas
		}
		return new ResponseEntity<>("1", HttpStatus.OK);
	}
	
	@PostMapping("/guardarAbonoCuota/")
	@ResponseBody
	public ResponseEntity<String> guardarAbonoCuota(Model model, HttpSession session,
			Integer idPrestamo, Double monto, Integer tipoCuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Amortizacion> detalles = new LinkedList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		double disponible = monto;
		double totalMora = 0;
		
		if (tipoCuota == 1) {
			if (prestamo.getTipo().equals("2")) {
				List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles
						.buscarPorPrestamo(prestamo);

				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
					if (prestamoInteresDetalle.getPagado().doubleValue() == 0) {
						// Pagamos los cargos que no esten pagados
						List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota())
								.stream().filter(c -> c.getEstado() == 0).collect(Collectors.toList());
						if (disponible > 0) {
							if (!prestamosAdicionales.isEmpty()) {
								for (PrestamoAdicional adicionales : prestamosAdicionales) {
								  double tempMonto = adicionales.getMonto()-adicionales.getDescuento_adicionales();
									if (disponible > 0) {
										if (adicionales.getMonto_pagado() < tempMonto) {
											if (disponible + adicionales.getMonto_pagado() >= tempMonto) {
												if (disponible + adicionales.getMonto_pagado()
														.doubleValue() == tempMonto) {
													adicionales.setMonto_pagado(
															disponible + adicionales.getMonto_pagado());
													disponible = 0;
												} else {
													double montoTemp = tempMonto
															- adicionales.getMonto_pagado();
													adicionales.setMonto_pagado(tempMonto);
													disponible -= montoTemp;
												}
											} else {
												if (adicionales.getMonto_pagado().doubleValue() == tempMonto) {
													adicionales.setEstado(1);
													continue;
												} else {
													adicionales.setMonto_pagado(
															adicionales.getMonto_pagado() + disponible);
													disponible = 0;
												}
											}
											if (adicionales.getMonto_pagado().doubleValue() == tempMonto) {
												adicionales.setEstado(1);
											}
											servicePrestamosAdicionales.guardar(adicionales);
										}
									}
								}
							}
						}
						
						double tempMora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getDescuento_mora();
						//Pagamos las moras
						if (disponible > 0) {
							if (prestamoInteresDetalle.getMora_pagada() < tempMora) {
								if (disponible + prestamoInteresDetalle.getMora_pagada() >= tempMora) {
									if (disponible + prestamoInteresDetalle.getMora_pagada().doubleValue() == tempMora) {
										prestamoInteresDetalle.setMora_pagada(disponible + prestamoInteresDetalle.getMora_pagada());
										disponible = 0;
									} else {
										double montoTemp = tempMora - prestamoInteresDetalle.getMora_pagada();
										prestamoInteresDetalle.setMora_pagada(tempMora);
										disponible -= montoTemp;
									}
								} else {
									if (prestamoInteresDetalle.getMora_pagada().doubleValue() == tempMora) {
										continue;
									} else {
										prestamoInteresDetalle.setMora_pagada(prestamoInteresDetalle.getMora_pagada() + disponible);
										disponible = 0;
									}
								}
								if (prestamoInteresDetalle.getMora_pagada().doubleValue() == tempMora) {
								}
								servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
							}
						}
					
						//Pagamos el interes
						if(disponible>0) {
							if (prestamoInteresDetalle.getInteres_pagado() < prestamoInteresDetalle
									.getInteres()) {
								if (disponible + prestamoInteresDetalle
										.getInteres_pagado() >= prestamoInteresDetalle.getInteres()) {
									if (disponible + prestamoInteresDetalle.getInteres_pagado()
											.doubleValue() == prestamoInteresDetalle.getInteres().doubleValue()) {
										prestamoInteresDetalle.setInteres_pagado(
												disponible + prestamoInteresDetalle.getInteres_pagado());
										disponible = 0;
									} else {
										double interesTemp = prestamoInteresDetalle.getInteres()
												- prestamoInteresDetalle.getInteres_pagado();
										prestamoInteresDetalle
												.setInteres_pagado(prestamoInteresDetalle.getInteres());
										disponible -= interesTemp;
									}
								} else {
									if (prestamoInteresDetalle.getInteres_pagado()
											.doubleValue() == prestamoInteresDetalle.getInteres().doubleValue()) {
										continue;
									} else {
										prestamoInteresDetalle.setInteres_pagado(
												prestamoInteresDetalle.getInteres_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
							}
						}
					}
				}
				
				//Antes de pagar el Capital volvemos a verificar los cargos por prestamo solamente
				if (disponible > 0) {
					// Pagamos los cargos que no esten pagados
					List<PrestamoAdicional> adicionalesTemps = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
					if (!adicionalesTemps.isEmpty()) {
						for (PrestamoAdicional adicionalesTemp : adicionalesTemps) {
							if (disponible > 0) {
								if (adicionalesTemp.getMonto_pagado() < adicionalesTemp.getMonto()) {
									if (disponible + adicionalesTemp.getMonto_pagado() >= adicionalesTemp.getMonto()) {
										if (disponible + adicionalesTemp.getMonto_pagado()
												.doubleValue() == adicionalesTemp.getMonto().doubleValue()) {
											adicionalesTemp.setMonto_pagado(
													disponible + adicionalesTemp.getMonto_pagado());
											disponible = 0;
										} else {
											double montoTemp = adicionalesTemp.getMonto()
													- adicionalesTemp.getMonto_pagado();
											adicionalesTemp.setMonto_pagado(adicionalesTemp.getMonto());
											disponible -= montoTemp;
										}
									} else {
										if (adicionalesTemp.getMonto_pagado().doubleValue() == adicionalesTemp
												.getMonto().doubleValue()) {
											adicionalesTemp.setEstado(1);
											continue;
										} else {
											adicionalesTemp.setMonto_pagado(
													adicionalesTemp.getMonto_pagado() + disponible);
											disponible = 0;
										}
									}
									if (adicionalesTemp.getMonto_pagado().doubleValue() == adicionalesTemp.getMonto()
											.doubleValue()) {
										adicionalesTemp.setEstado(1);
									}
									servicePrestamosAdicionales.guardar(adicionalesTemp);
								}
							}
						}
					}
				}
								
//				Pagamos el capital
				if(disponible>0) {
					if (prestamo.getCapitalPagado() <= prestamo.getBalance()) {
						if (disponible + prestamo.getCapitalPagado() >= prestamo.getBalance()) {
							prestamo.setCapitalPagado(prestamo.getBalance());
							disponible -= prestamo.getCapitalPagado();
						} else {
							prestamo.setCapitalPagado(prestamo.getCapitalPagado() + disponible);
							disponible = 0;
						}
						prestamo.setBalance(prestamo.getMonto() - prestamo.getCapitalPagado());
						servicePrestamos.guardar(prestamo);
					}else {
						prestamo.setCapitalPagado(prestamo.getCapitalPagado()+disponible);
						prestamo.setBalance(prestamo.getMonto() - prestamo.getCapitalPagado());
						disponible= 0;
						servicePrestamos.guardar(prestamo);
					}
				}
				
				for (PrestamoInteresDetalle prestamoDetalles : prestamoInteresDetalles) {
					if(
						prestamoDetalles.getInteres_pagado().equals(prestamoDetalles.getInteres()) && 
						formato2d(prestamoDetalles.getMora_pagada().doubleValue()+prestamoDetalles.getDescuento_mora().doubleValue()) == formato2d(prestamoDetalles.getMora())) {
						prestamoDetalles.setEstadoPago(1);
						prestamoDetalles.setEstado(1);
						servicePrestamosInteresesDetalles.guardar(prestamoDetalles);
					}
				}
				
				if(prestamo.getBalance().doubleValue() == 0) {
					prestamo.setEstado(1);
					servicePrestamos.guardar(prestamo);
				}
				
			}else {
				//Cuotas
				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);

				//Pagamos los cargos
				List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				if(!prestamosAdicionales.isEmpty()) {
					for (PrestamoAdicional prestamoAdicional : prestamosAdicionales) {
						if (disponible + prestamoAdicional.getMonto_pagado() >= prestamoAdicional.getMonto()) {
							prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto());
							prestamoAdicional.setEstado(1);
							disponible -= prestamoAdicional.getMonto_pagado();
						} else {
							prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto_pagado() + disponible);
							disponible = 0;
						}
						servicePrestamosAdicionales.guardar(prestamoAdicional);
					}
				}
				
				//Pagamos las moras
				if(disponible>0) {
					if(!prestamoDetalles.isEmpty()) {
						for (PrestamoDetalle prestamoDetallesTemp : prestamoDetalles) {
							if(disponible>0) {
								if(prestamoDetallesTemp.getMora()>0) {
									if(prestamoDetallesTemp.getMora_pagada().doubleValue() < prestamoDetallesTemp.getMora().doubleValue()) {
										if(prestamoDetallesTemp.getMora_pagada().doubleValue() == prestamoDetallesTemp.getMora().doubleValue()) {
											prestamoDetallesTemp.setMora_pagada(disponible+prestamoDetallesTemp.getMora_pagada());
											disponible=0;
										}else {
											double moraTemp = prestamoDetallesTemp.getMora() - prestamoDetallesTemp.getMora_pagada();
											prestamoDetallesTemp.setMora_pagada(prestamoDetallesTemp.getMora());
											disponible-=moraTemp;
										}	
									}else {
										if(prestamoDetallesTemp.getMora_pagada().doubleValue() == prestamoDetallesTemp.getMora().doubleValue()) {
											continue;
										}else {
											prestamoDetallesTemp.setMora_pagada(prestamoDetallesTemp.getMora_pagada()+disponible);
											disponible=0;
										}
									}
									servicePrestamosDetalles.guardar(prestamoDetallesTemp);
								}
							}else {
								break;
							}
						}
					}
				}
								
				if (disponible > 0) {
					for (PrestamoDetalle prestamoDetalleTemp : prestamoDetalles) {
						if (disponible > 0) {
							// Pagamos el interes (Busca el interes de la cuota)
							if (prestamoDetalleTemp.getInteres_pagado() < prestamoDetalleTemp.getInteres()) {
								if (disponible + prestamoDetalleTemp.getInteres_pagado() >= prestamoDetalleTemp
										.getInteres()) {
									if (disponible + prestamoDetalleTemp.getInteres_pagado()
											.doubleValue() == prestamoDetalleTemp.getInteres().doubleValue()) {
										prestamoDetalleTemp.setInteres_pagado(
												disponible + prestamoDetalleTemp.getInteres_pagado());
										disponible = 0;
									} else {
										double interesTemp = prestamoDetalleTemp.getInteres()
												- prestamoDetalleTemp.getInteres_pagado();
										prestamoDetalleTemp.setInteres_pagado(prestamoDetalleTemp.getInteres());
										disponible -= interesTemp;
									}
								} else {
									if (prestamoDetalleTemp.getInteres_pagado().doubleValue() == prestamoDetalleTemp
											.getInteres().doubleValue()) {
										continue;
									} else {
										prestamoDetalleTemp.setInteres_pagado(
												prestamoDetalleTemp.getInteres_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalleTemp);
							}
							
							//Pagamos el capital de la cuota
							if (prestamoDetalleTemp.getCapital_pagado() < prestamoDetalleTemp.getCapital()) {
								if (disponible + prestamoDetalleTemp.getCapital_pagado() >= prestamoDetalleTemp
										.getCapital()) {
									if (disponible + prestamoDetalleTemp.getCapital_pagado()
											.doubleValue() == prestamoDetalleTemp.getCapital().doubleValue()) {
										prestamoDetalleTemp.setCapital_pagado(
												disponible + prestamoDetalleTemp.getCapital_pagado());
										disponible = 0;
									} else {
										double capitalTemp = prestamoDetalleTemp.getCapital()
												- prestamoDetalleTemp.getCapital_pagado();
										prestamoDetalleTemp.setCapital_pagado(prestamoDetalleTemp.getCapital());
										disponible -= capitalTemp;
									}
								} else {
									if (prestamoDetalleTemp.getCapital_pagado().doubleValue() == prestamoDetalleTemp
											.getCapital().doubleValue()) {
										continue;
									} else {
										prestamoDetalleTemp.setCapital_pagado(
												prestamoDetalleTemp.getCapital_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalleTemp);
							}

						} else {
							break;
						}
					}
				}
				
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					if(
						prestamoDetalle.getCapital_pagado().equals(prestamoDetalle.getCapital()) &&
						prestamoDetalle.getInteres_pagado().equals(prestamoDetalle.getInteres()) && 
						prestamoDetalle.getMora_pagada().equals(prestamoDetalle.getMora())) {
						prestamoDetalle.setEstado(1);
						prestamoDetalle.setEstado_cuota("Saldo");
						prestamoDetalle.setDias_atraso(0);
						servicePrestamosDetalles.guardar(prestamoDetalle);
					}
				}
			}
		}
		
		return new ResponseEntity<>("1", HttpStatus.OK);
	}

	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public double diasVencidos(LocalDateTime fecha1, LocalDateTime fecha2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         String fecha1Temp = fecha1.getDayOfMonth()+"/"+fecha1.getMonthValue()+"/"+fecha1.getYear();
         String fecha2Temp = fecha2.getDayOfMonth()+"/"+fecha2.getMonthValue()+"/"+fecha2.getYear();
         Date dt1 = sdf.parse(fecha1Temp);
         Date dt2 = sdf.parse(fecha2Temp);
         long diff = dt2.getTime() - dt1.getTime();
         return diff / 1000L / 60L / 60L / 24L;
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	public String fecha_futuro(String fecha_pago, String forma_pago) throws ParseException {
		    String[] fechaTemp = null;
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  

			if (fecha_pago.contains("-")) {
				String[] temporal = fecha_pago.split("-");
				if (temporal.length == 3) {
					String dia = temporal[0];
					if (!dia.contains("0")) {
						dia = "0" + dia;
					}
					String mes = temporal[1];
					if (!mes.contains("0")) {
						mes = "0" + mes;
					}
					String temp = dia + "-" + mes + "-" + temporal[2];
					fechaTemp = temp.split("-");
				}

			} else if (fecha_pago.contains("/")) {

				String[] temporal = fecha_pago.split("/");
				if (temporal.length == 3) {
					String dia = temporal[0];
					if (!dia.contains("0")) {
						dia = "0" + dia;
					}
					String mes = temporal[1];
					if (!mes.contains("0")) {
						mes = "0" + mes;
					}
					String temp = dia + "/" + mes + "/" + temporal[2];
					fechaTemp = temp.split("/");
				}

			}
			
		Date date =	sdf.parse(fechaTemp[0]+"/"+fechaTemp[1]+"/"+fechaTemp[2]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(forma_pago));
		Date newDate = calendar.getTime();
		String fecha = sdf.format(newDate);
		return fecha;
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
