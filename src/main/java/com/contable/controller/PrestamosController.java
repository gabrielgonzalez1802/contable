package com.contable.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.contable.model.Abono;
import com.contable.model.AbonoDetalle;
import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Cuenta;
import com.contable.model.DescuentoDetalle;
import com.contable.model.DetalleMultiPrestamo;
import com.contable.model.DetalleReporteAbono;
import com.contable.model.Empresa;
import com.contable.model.MoraDetalle;
import com.contable.model.MotivoPrestamoAdicional;
import com.contable.model.Nota;
import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoDetalleMixto;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.PrestamoTipo;
import com.contable.model.Usuario;
import com.contable.service.IAbonosDetallesService;
import com.contable.service.IAbonosService;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.ICuentasService;
import com.contable.service.IDescuentosDetallesService;
import com.contable.service.IEmpresasService;
import com.contable.service.IMotivosPrestamosAdicionalesService;
import com.contable.service.INotasService;
import com.contable.service.IPagosTempService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;
import com.contable.service.IPrestamosTiposService;
import com.contable.util.Numero_Letras;
import com.contable.util.Utileria;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
	
	@Autowired
	private IAbonosService serviceAbonos;
	
	@Autowired
	private IAbonosDetallesService serviceAbonosDetalles;
	
	@Autowired
	private INotasService serviceNotas;
	
	@Autowired
	private IPagosTempService servicePagosTemp;
	
	@Autowired
	private IEmpresasService serviceEmpresas;
	
	@Autowired
	private IMotivosPrestamosAdicionalesService serviceMotivosPrestamosAdicionales;
	
	@Autowired
	private IPrestamosTiposService servicePrestamosTipos;
	
	@Autowired
	private IDescuentosDetallesService serviceDescuentosDetalles;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	@Value("${contable.ruta.reporte.abono}")
	private String rutaJreportAbono;
	
	@Value("${contable.ruta.imagenes}")
	private String ruta;
	
	@Autowired
	private DataSource dataSource;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/")
	public String listaPrestamos(Model model) {
		return "prestamos/listaPrestamos :: listaPrestamo";
	}
	
	@GetMapping("/agregar")
	public String agregarPrestamos(Model model, HttpSession session) {
		Prestamo prestamo = new Prestamo();
		Cliente cliente = new Cliente();
		List<PrestamoTipo> prestamosTipos = servicePrestamosTipos.buscarTodos();
		prestamo.setCuenta(new Cuenta());
		Carpeta carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		if(!cuentas.isEmpty()) {
			for (Cuenta cuenta : cuentas) {
				BigDecimal monto = new BigDecimal(cuenta.getMonto());
				cuenta.setBanco(cuenta.getBanco()+" - "+monto.toPlainString());
			}
		}
		if((Integer)session.getAttribute("cliente")==0) {
			model.addAttribute("prestamo",  new Prestamo());
			model.addAttribute("prestamosTipos", prestamosTipos);
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
		model.addAttribute("prestamosTipos", prestamosTipos);
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
		
		prestamo.setEmpresa((Empresa) session.getAttribute("empresa"));
		
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
				int x = 0;
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
					x++;
					amortizacion.setFecha(fecha_pago);
					amortizacion.setNumero(x);
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
		
		MotivoPrestamoAdicional motivoPrestamoAdicional = serviceMotivosPrestamosAdicionales.buscarPorMotivo("Gastos Cierre");
		
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
					prestamoAdicional.setMotivo(motivoPrestamoAdicional);
					prestamoAdicional.setPrestamo(prestamo);
					prestamoAdicional.setPrestamoDetalle(prestamosDetalles.get(i));
					prestamoAdicional.setNumeroCuota(prestamosDetalles.get(i).getNumero());
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
					prestamoAdicional.setMotivo(motivoPrestamoAdicional);
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
		
		if(response == 1) {
			if(!prestamo.getTipo().equals("2")) {
				//Cuotas
				double capitalTemp = 0;
				List<PrestamoDetalle> detallesTemp = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoDetalle prestamoDetalle : detallesTemp) {
					capitalTemp+=prestamoDetalle.getCapital();
				}
				if(!detallesTemp.isEmpty()) {
					double montoTemp = prestamo.getMonto()-capitalTemp;
					PrestamoDetalle detalleTemp = detallesTemp.get(detallesTemp.size()-1);
					detalleTemp.setCapital(detalleTemp.getCapital()+montoTemp);
					detalleTemp.setInteres(detalleTemp.getInteres()-montoTemp);
					servicePrestamosDetalles.guardar(detalleTemp);
				}
			}
		}
		
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/getTipoPrestamo/{id}")
	public ResponseEntity<String> getTipoPrestamo(HttpSession session, Model model, @PathVariable("id") Integer id) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		String response = "0";
		if(prestamo!=null) {
			response = prestamo.getPrestamoTipo().getTipo().toLowerCase();
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/cargarImagenes")
	public ResponseEntity<String> cargarImagenes(@RequestParam("files") MultipartFile[] files){
		System.out.println("aqui");
		return new ResponseEntity<String>("", HttpStatus.ACCEPTED);
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
					}else if(prestamoInteresDetalle.getEstado()==3) {
						estado = "Legal";
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
			
			int count = 0;
			double mora = 0;
			double interes = 0;
			double capital = 0;

			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
			
			detalles = new LinkedList<>();
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				
				List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

				Amortizacion amortizacion = new Amortizacion();
				
				double cargo = 0;
				
				count++;
				String estado = "Normal";
				if(prestamoDetalle.getEstado()==2) {
					estado = "Atraso";
				}else if(prestamoDetalle.getEstado()==1) {
					estado = "Saldo";
				}
				
				double cargoPagadoTemp = 0;
				double descuentoAdicionales = 0;
				double descuentoCargo = 0;
				
				if(!adicionales.isEmpty()) {
					for (PrestamoAdicional adicionalTemp : adicionales) {
						cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
						cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
						descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
					}
					amortizacion.setCargo(cargo);
					descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
					amortizacion.setCargo(formato2d(cargo-descuentoAdicionales));
				}
				
				double abono = prestamoDetalle.getCapital_pagado()+prestamoDetalle.getInteres_pagado()+prestamoDetalle.getMora_pagada()+cargoPagadoTemp;
				
				double descuentoAdicional = 0;
				
				List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
						.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero());
				for (PrestamoAdicional adicional : adicionalTemp) {
					descuentoAdicional += adicional.getDescuento_adicionales();
				}

				amortizacion.setDescuento(formato2d(prestamoDetalle.getDescuento_mora()+descuentoAdicional));
				
				amortizacion.setAbono(formato2d(abono));
				amortizacion.setId(prestamoDetalle.getId());
				amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
				amortizacion.setCuota(prestamoDetalle.getCuota());
				amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
				amortizacion.setCapital(formato2d(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado()));
				amortizacion.setInteres(formato2d(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado()));
				amortizacion.setSaldo(formato2d(prestamoDetalle.getBalance()));
				amortizacion.setNumero(prestamoDetalle.getNumero());
				amortizacion.setMora(formato2d(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada()-prestamoDetalle.getDescuento_mora()));
				amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
				amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
				amortizacion.setBalance(formato2d((amortizacion.getCapital()==null?0:amortizacion.getCapital()) + (amortizacion.getInteres()==null?0:amortizacion.getInteres()) + (amortizacion.getMora()==null?0:amortizacion.getMora()) + (amortizacion.getCargo()==null?0:amortizacion.getCargo())));
				detalles.add(amortizacion);
			}
		}
		
		Integer idCliente; 
				
		if(session.getAttribute("cliente")!=null) {
			if((Integer) session.getAttribute("cliente") == 0) {
				idCliente = prestamo.getCliente().getId();
			}else {
				idCliente = (Integer) session.getAttribute("cliente");
			}
		}else {
			idCliente = prestamo.getCliente().getId();
		}

		Cliente cliente = serviceClientes.buscarPorId(idCliente);
		String datosCliente  = cliente.getNombre()+" / Tel.: "+cliente.getTelefono();
		
		model.addAttribute("detalles", detalles);
		model.addAttribute("totalCuota", prestamo.getTotal_cuota());
		model.addAttribute("totalCapital", prestamo.getTotal_capital());
		model.addAttribute("totalInteres", prestamo.getTotal_interes());
		model.addAttribute("totalNeto", prestamo.getTotal_neto());
		model.addAttribute("numeroPrestamo", "No. Prestamo: "+prestamo.getCodigo());
		model.addAttribute("montoPrestamo", "Prestado: "+prestamo.getMonto());
		if(prestamo.getTipo().equals("2")) {
			model.addAttribute("sumaInteresGen", formato2d(sumaInteresGen));
			model.addAttribute("capitalPendiente", "Capital Pendiente: "+prestamo.getBalance());
			model.addAttribute("sumaCapital", "");
			model.addAttribute("sumaMora", formato2d(sumaMora));
			model.addAttribute("sumaCargo", formato2d(sumaCargo));
			model.addAttribute("sumaAbono", formato2d(sumaAbono));
			model.addAttribute("sumaAbonoCuota", "");
			model.addAttribute("sumaBalance", formato2d(sumaBalance));
			model.addAttribute("totales", "");
		}
		model.addAttribute("totalPendiente", "Total Pendiente: "+totalPendiente);
		model.addAttribute("datosCliente", datosCliente);
				
		double sumaCapital = 0;
		double montoCuota = 0;
		double sumaMoraTemp = 0;
		double sumaInteres = 0;
		double sumaCargos = 0;
		double sumaAbonoCuota = 0;
		double sumaDescuento = 0;
		double sumaTotales = 0;
		
		for (Amortizacion detalle : detalles) {
			sumaCapital+=detalle.getCapital()==null?0:detalle.getCapital();
			montoCuota+=detalle.getCuota()==null?0:detalle.getCuota();
			sumaMoraTemp+=detalle.getMora()==null?0:detalle.getMora();
			sumaInteres+=detalle.getInteres()==null?0:detalle.getInteres();
			sumaCargos+=detalle.getCargo()==null?0:detalle.getCargo();
			sumaAbonoCuota+=detalle.getAbono()==null?0:detalle.getAbono();
			sumaDescuento+=detalle.getDescuento()==null?0:detalle.getDescuento();
			sumaTotales+=detalle.getBalance()==null?0:detalle.getBalance();
		}

		if(!prestamo.getTipo().equals("2")) {
			model.addAttribute("sumaInteresGen", formato2d(montoCuota));
			model.addAttribute("sumaCapital", formato2d(sumaCapital));
			model.addAttribute("capitalPendiente", "Capital Pendiente: "+ formato2d(sumaCapital));
			model.addAttribute("sumaMora", formato2d(sumaInteres));
			model.addAttribute("sumaCargo", formato2d(sumaMoraTemp));
			model.addAttribute("sumaAbono", formato2d(sumaCargos));
			model.addAttribute("sumaAbonoCuota", formato2d(sumaAbonoCuota));
			model.addAttribute("sumaBalance", formato2d(sumaDescuento));
			model.addAttribute("totales", formato2d(sumaTotales));
		}
		
		model.addAttribute("tipoPrestamo", prestamo.getTipo());
		model.addAttribute("capitalPendienteTemp", formato2d(prestamo.getBalance()));
		return "index :: #cuerpo_amortizacion";
	}
	
	@GetMapping("/detallesCargos/{id}/{prestamoId}")
	public String detallesCargos(Model model, @PathVariable("id") Integer id, @PathVariable("prestamoId") Integer prestamoId) {
		List<DetalleMultiPrestamo> adicionales = new LinkedList<>();
		Prestamo prestamo = servicePrestamos.buscarPorId(prestamoId);
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(id);
			if(prestamoInteresDetalle != null) {
				List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales.
						buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());

				if(prestamosAdicionales.isEmpty()) {
					prestamosAdicionales = servicePrestamosAdicionales.
							buscarPorPrestamo(prestamoInteresDetalle.getPrestamo());
				}
				
				for (PrestamoAdicional prestamoAdicional : prestamosAdicionales) {
					DetalleMultiPrestamo multiPrestamo = new DetalleMultiPrestamo();
					multiPrestamo.setFecha(prestamoAdicional.getFecha());
					multiPrestamo.setMonto(prestamoAdicional.getMonto());
					multiPrestamo.setDescuento_adicionales(prestamoAdicional.getDescuento_adicionales());
					multiPrestamo.setMonto_pagado(prestamoAdicional.getMonto_pagado());
					multiPrestamo.setMotivo(prestamoAdicional.getMotivo().getMotivo());
					multiPrestamo.setNota(prestamoAdicional.getNota());
					adicionales.add(multiPrestamo);
				}
			}
		}else {
			//Cuotas
			PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(id);
			if(prestamoDetalle != null) {
				List<PrestamoAdicional> adicionalesCuotas = servicePrestamosAdicionales.
						buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

				if(adicionalesCuotas.isEmpty()) {
					adicionalesCuotas = servicePrestamosAdicionales.
							buscarPorPrestamo(prestamoDetalle.getPrestamo());
				}
				
				for (PrestamoAdicional adicionalCuota : adicionalesCuotas) {
					DetalleMultiPrestamo multiPrestamo = new DetalleMultiPrestamo();
					multiPrestamo.setFecha(adicionalCuota.getFecha());
					multiPrestamo.setMonto(adicionalCuota.getMonto());
					multiPrestamo.setDescuento_adicionales(adicionalCuota.getDescuento_adicionales());
					multiPrestamo.setMonto_pagado(adicionalCuota.getMonto_pagado());
					multiPrestamo.setMotivo(adicionalCuota.getMotivo().getMotivo());
					multiPrestamo.setNota(adicionalCuota.getNota());
					adicionales.add(multiPrestamo);
				}
			}
		}
		
		model.addAttribute("cargos", adicionales);
		return "index :: #tablaCargos";
	}
	
	@GetMapping("/mostrarCotasPrestamo/{id}")
	public String mostrarCotasPrestamo(@PathVariable(name = "id") Integer idPrestamo, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		List<DetalleMultiPrestamo> detalleMultiPrestamos = new LinkedList<>();
		if(prestamo.getTipo().equals("2")) {
			//Interes
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
				DetalleMultiPrestamo detalleMultiPrestamo = new DetalleMultiPrestamo();
				detalleMultiPrestamo.setId(prestamoInteresDetalle.getId());
				detalleMultiPrestamo.setNota(prestamoInteresDetalle.getNumero_cuota().toString());
				detalleMultiPrestamos.add(detalleMultiPrestamo);
			}
		}else {
			//Cuotas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				DetalleMultiPrestamo detalleMultiPrestamo = new DetalleMultiPrestamo();
				detalleMultiPrestamo.setId(prestamoDetalle.getId());
				detalleMultiPrestamo.setNota(prestamoDetalle.getNumero().toString());
				detalleMultiPrestamos.add(detalleMultiPrestamo);
			}
		}
		model.addAttribute("cuotas",detalleMultiPrestamos);
		return "notas/infoNotas :: #cuotaNota";
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
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			if(cuotasArray.length>0) {
				for (String id : cuotasArray) {
					PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(Integer.parseInt(id));
					
						String estado = "Normal";
						if(prestamoInteresDetalle.getEstado()==2) {
							estado = "Atraso";
						}else if(prestamoInteresDetalle.getEstado()==1) {
							estado = "Saldo";
						}else if(prestamoInteresDetalle.getEstado()==3) {
							estado = "Legal";
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
						sumaCargo += amortizacion.getCargo()==null?0.0:amortizacion.getCargo();
											
					detalles.get(0).setCuota(formato2d(mora+interes));
					detalles.get(0).setCapital(formato2d(capital));
				}
			}
		}else {
			//Cuota
			detalles = new LinkedList<>();
			if(cuotasArray.length>0) {
				for (String id : cuotasArray) {
					PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(Integer.parseInt(id));
					
					//Cuotas
					
					int count = 0;
					double mora = 0;
					double interes = 0;
					double capitalTemp = 0;

					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

					Amortizacion amortizacion = new Amortizacion();
						
					double cargoTemp = 0;
						
					count++;
					String estado = "Normal";
					if(prestamoDetalle.getEstado()==2) {
						estado = "Atraso";
					}else if(prestamoDetalle.getEstado()==1) {
						estado = "Saldo";
					}
						
					double cargoPagadoTemp = 0;
					double descuentoAdicionales = 0;
					double descuentoCargo = 0;
						
					if (!adicionales.isEmpty()) {
						for (PrestamoAdicional adicionalTemp : adicionales) {
							cargo += adicionalTemp.getMonto() - adicionalTemp.getMonto_pagado();
							cargoPagadoTemp += adicionalTemp.getMonto_pagado();
							descuentoAdicionales += adicionalTemp.getDescuento_adicionales();
						}
						amortizacion.setCargo(cargo);
						descuentoCargo = amortizacion.getCargo() - descuentoAdicionales;
						amortizacion.setCargo(formato2d(cargo - descuentoAdicionales));
					}

					double abono = prestamoDetalle.getCapital_pagado()+prestamoDetalle.getInteres_pagado()+prestamoDetalle.getMora_pagada()+cargoPagadoTemp;
						
					double descuentoAdicional = 0;
						
					List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
							.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero());
					for (PrestamoAdicional adicional : adicionalTemp) {
						descuentoAdicional += adicional.getDescuento_adicionales();
					}

					amortizacion.setDescuento(formato2d(prestamoDetalle.getDescuento_mora() + descuentoAdicional));

					amortizacion.setAbono(formato2d(abono));
					amortizacion.setId(prestamoDetalle.getId());
					amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
					amortizacion.setCuota(prestamoDetalle.getCuota());
					amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
					amortizacion
							.setCapital(formato2d(prestamoDetalle.getCapital() - prestamoDetalle.getCapital_pagado()));
					amortizacion
							.setInteres(formato2d(prestamoDetalle.getInteres() - prestamoDetalle.getInteres_pagado()));
					amortizacion.setSaldo(formato2d(prestamoDetalle.getBalance()));
					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(formato2d(prestamoDetalle.getMora() - prestamoDetalle.getMora_pagada()
							- prestamoDetalle.getDescuento_mora()));
					amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
					amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
					amortizacion
							.setBalance(formato2d((amortizacion.getCapital() == null ? 0 : amortizacion.getCapital())
									+ (amortizacion.getInteres() == null ? 0 : amortizacion.getInteres())
									+ (amortizacion.getMora() == null ? 0 : amortizacion.getMora())
									+ (amortizacion.getCargo() == null ? 0 : amortizacion.getCargo())));
					detalles.add(amortizacion);

				}
			}
		}
		
		sumaDescuento = 0;
		
		for (Amortizacion amortizacion : detalles) {
			sumaDescuento+=amortizacion.getDescuento();
			sumaMora+=amortizacion.getMora();
			sumaCargo+= amortizacion.getCargo()==null?0:amortizacion.getCargo();
			sumaBalance+=amortizacion.getBalance();
		}
		
		model.addAttribute("totalDescuento", formato2d(sumaDescuento));
		model.addAttribute("totalMora", formato2d(sumaMora));
		model.addAttribute("totalCargo", formato2d(sumaCargo));
		model.addAttribute("totalBalance", formato2d(sumaBalance));
		
		return "clientes/infoCliente :: #totalesGeneralesPrestamo";
	}
	
	@GetMapping("/detallesMoras/{id}/{prestamoId}")
	public String detallesMoras(Model model, @PathVariable("id") Integer id, @PathVariable("prestamoId") Integer prestamoId) {
		Prestamo prestamo = servicePrestamos.buscarPorId(prestamoId);
		List<MoraDetalle> moraDetalles = new LinkedList<>();
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(id);
			if(prestamoInteresDetalle != null) {
				MoraDetalle moraDetalle = new MoraDetalle();
				moraDetalle.setId(prestamoInteresDetalle.getId());
				moraDetalle.setDescuento_mora(prestamoInteresDetalle.getDescuento_mora());
				moraDetalle.setMora(prestamoInteresDetalle.getMora());
				moraDetalle.setMora_pagada(prestamoInteresDetalle.getMora_pagada());
				moraDetalles.add(moraDetalle);
			}
		}else {
			//Cuotas
			PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(id);
			if(prestamoDetalle != null) {
				MoraDetalle moraDetalle = new MoraDetalle();
				moraDetalle.setId(prestamoDetalle.getId());
				moraDetalle.setDescuento_mora(prestamoDetalle.getDescuento_mora());
				moraDetalle.setMora(formato2d(formato2d(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada())-prestamoDetalle.getDescuento_mora()));
				moraDetalle.setMora_pagada(prestamoDetalle.getMora_pagada());
				moraDetalles.add(moraDetalle);
			}
		}
		
		model.addAttribute("detalles", moraDetalles);
		return "index :: #tablaMoras";
	}
	
	@GetMapping("/detallesCapitales/{id}/{prestamoId}")
	public String detallesCapitales(Model model, @PathVariable("id") Integer id, @PathVariable("prestamoId") Integer prestamoId) {
		Prestamo prestamo = servicePrestamos.buscarPorId(prestamoId);
		List<DetalleMultiPrestamo> multiPrestamos = new LinkedList<>();
		PrestamoDetalle prestamoDetalle = new PrestamoDetalle();
		if(prestamo.getTipo().equals("2")) {
			//Interes
		}else {
			//Cuotas
			List<Abono> abonos = serviceAbonos.buscarPorPrestamo(prestamo);
			prestamoDetalle = servicePrestamosDetalles.buscarPorId(id);
			for (Abono abono : abonos) {
				List<AbonoDetalle> abonosDetalles = serviceAbonosDetalles.buscarPorAbonoConcepto(abono, "Capital");
				for (AbonoDetalle abonoDetalle : abonosDetalles) {
					if (prestamoDetalle.getNumero() == abonoDetalle.getNumeroCuota()) {
							DetalleMultiPrestamo detalleMultiPrestamo = new DetalleMultiPrestamo();
							detalleMultiPrestamo.setId(abono.getId());
							detalleMultiPrestamo.setFecha(abono.getFecha());
							detalleMultiPrestamo.setCapital(prestamoDetalle.getCapital());
							detalleMultiPrestamo.setCapital_pagado(abonoDetalle.getMonto());
							multiPrestamos.add(detalleMultiPrestamo);
					}
				}
			}
		}
		
		model.addAttribute("capitalGenerado", prestamoDetalle.getCapital());
		model.addAttribute("capitalPagado", prestamoDetalle.getCapital_pagado());
		model.addAttribute("balanceCapital",prestamoDetalle.getCapital() - prestamoDetalle.getCapital_pagado());
		model.addAttribute("detalles", multiPrestamos);
		return "index :: #capitalInfo";
	}
	
	@GetMapping("/detallesIntereses/{id}/{prestamoId}")
	public String detallesIntereses(Model model, @PathVariable("id") Integer id, @PathVariable("prestamoId") Integer prestamoId) {
		Prestamo prestamo = servicePrestamos.buscarPorId(prestamoId);
		List<DetalleMultiPrestamo> multiPrestamos = new LinkedList<>();
		PrestamoDetalle prestamoDetalle = new PrestamoDetalle();
		PrestamoInteresDetalle prestamoInteresDetalle = new PrestamoInteresDetalle();
		if(prestamo.getTipo().equals("2")) {
			//Interes
			List<Abono> abonos = serviceAbonos.buscarPorPrestamo(prestamo);
			prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(id);
			for (Abono abono : abonos) {
				List<AbonoDetalle> abonosDetalles = serviceAbonosDetalles.buscarPorAbonoConcepto(abono, "Interes");
				for (AbonoDetalle abonoDetalle : abonosDetalles) {
					if (prestamoInteresDetalle.getNumero_cuota() == abonoDetalle.getNumeroCuota()) {
							DetalleMultiPrestamo detalleMultiPrestamo = new DetalleMultiPrestamo();
							detalleMultiPrestamo.setId(abono.getId());
							detalleMultiPrestamo.setFecha(abono.getFecha());
							detalleMultiPrestamo.setInteres(prestamoInteresDetalle.getInteres());
							detalleMultiPrestamo.setInteres_pagado(abonoDetalle.getMonto());
							multiPrestamos.add(detalleMultiPrestamo);
					}
				}
			}
		}else {
			//Cuotas
			List<Abono> abonos = serviceAbonos.buscarPorPrestamo(prestamo);
			prestamoDetalle = servicePrestamosDetalles.buscarPorId(id);
			for (Abono abono : abonos) {
				List<AbonoDetalle> abonosDetalles = serviceAbonosDetalles.buscarPorAbonoConcepto(abono, "Interes");
				for (AbonoDetalle abonoDetalle : abonosDetalles) {
					if (prestamoDetalle.getNumero() == abonoDetalle.getNumeroCuota()) {
							DetalleMultiPrestamo detalleMultiPrestamo = new DetalleMultiPrestamo();
							detalleMultiPrestamo.setId(abono.getId());
							detalleMultiPrestamo.setFecha(abono.getFecha());
							detalleMultiPrestamo.setInteres(prestamoDetalle.getInteres());
							detalleMultiPrestamo.setInteres_pagado(abonoDetalle.getMonto());
							multiPrestamos.add(detalleMultiPrestamo);
					}
				}
			}
		}
		
		model.addAttribute("interesGenerado", !prestamo.getTipo().equals("2")?prestamoDetalle.getInteres():prestamoInteresDetalle.getInteres());
		model.addAttribute("interesPagado", !prestamo.getTipo().equals("2")?prestamoDetalle.getInteres_pagado():prestamoInteresDetalle.getInteres_pagado());
		model.addAttribute("balanceInteres",!prestamo.getTipo().equals("2")?prestamoDetalle.getInteres() - prestamoDetalle.getInteres_pagado():prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado());
		model.addAttribute("detalles", multiPrestamos);
		return "index :: #interesInfo";
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
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getEstado() != 1).collect(Collectors.toList());
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
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getEstado() != 1).collect(Collectors.toList());
			if(!prestamoDetalles.isEmpty()) {
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
					prestamoDetalleMixto.setId(prestamoDetalle.getId());
					prestamoDetalleMixto.setNumero(prestamoDetalle.getNumero());
					prestamoDetalleMixto.setFecha(prestamoDetalle.getFechaGenerada());
					prestamoDetalleMixto.setItem("Cuota "+prestamoDetalle.getNumero());
					prestamosDetallesMixto.add(prestamoDetalleMixto);
				}
			}
		}
		model.addAttribute("prestamoDetalles", prestamosDetallesMixto);
		return "clientes/infoCliente :: #selectCuotasMora";
	}
	
	@GetMapping("/cuotasNoPagadasDescuentosTemp/{id}/{tipo}")
	public String cuotasNoPagadasDescuentosTemp(Model model, @PathVariable("id") Integer id,
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
//			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getEstado() != 1).collect(Collectors.toList());
//			if(!prestamoDetalles.isEmpty()) {
//				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
//					PrestamoDetalleMixto prestamoDetalleMixto = new PrestamoDetalleMixto();
//					prestamoDetalleMixto.setId(prestamoDetalle.getId());
//					prestamoDetalleMixto.setNumero(prestamoDetalle.getNumero());
//					prestamoDetalleMixto.setFecha(prestamoDetalle.getFechaGenerada());
//					prestamoDetalleMixto.setItem("Cuota "+prestamoDetalle.getNumero());
//					prestamosDetallesMixto.add(prestamoDetalleMixto);
//				}
//			}
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
		
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		Carpeta carpeta = new Carpeta();
		
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
		}else {
			//Cargamos la principal
			if(session.getAttribute("empresa")!=null) {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
			}
		}
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
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
					
					DescuentoDetalle descuentoDetalle = new DescuentoDetalle();
					descuentoDetalle.setCarpeta(carpeta);
					descuentoDetalle.setEmpresa(empresa);
					descuentoDetalle.setFecha(new Date());
					descuentoDetalle.setCliente(prestamo.getCliente());
					descuentoDetalle.setUsuario(usuario);
					descuentoDetalle.setPrestamo(prestamo);
					descuentoDetalle.setConcepto("mora");
					descuentoDetalle.setTotalDescuento(monto);
					serviceDescuentosDetalles.guardar(descuentoDetalle);
					
					response = 1;
				}
			}else if(tipo == 2) {
				//Adicionales
				PrestamoAdicional adicional = servicePrestamosAdicionales.buscarPorId(cuota);
				adicional.setDescuento_adicionales(adicional.getDescuento_adicionales() + monto);
				servicePrestamosAdicionales.guardar(adicional);
				DescuentoDetalle descuentoDetalle = new DescuentoDetalle();
				descuentoDetalle.setCarpeta(carpeta);
				descuentoDetalle.setEmpresa(empresa);
				descuentoDetalle.setFecha(new Date());
				descuentoDetalle.setCliente(prestamo.getCliente());
				descuentoDetalle.setUsuario(usuario);
				descuentoDetalle.setPrestamo(prestamo);
				descuentoDetalle.setConcepto("adicionales");
				descuentoDetalle.setTotalDescuento(monto);
				serviceDescuentosDetalles.guardar(descuentoDetalle);
				response = 1;
				
				if(formato2d(adicional.getMonto().doubleValue()) == formato2d(adicional.getDescuento_adicionales()+adicional.getMonto_pagado())) {
					adicional.setEstado(1);
					servicePrestamosAdicionales.guardar(adicional);
				}
			}
		}else {
			//Cuotas
			if(tipo == 1) {
				//Mora
				List<PrestamoDetalle> detalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				if(!detalles.isEmpty()) {
					double disponible = monto;
					for (PrestamoDetalle prestamoDetalle : detalles) {
						double tempMora = formato2d(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada()-prestamoDetalle.getDescuento_mora());
//						double descuentoMoraInicio = disponible;
						//Descontamos de las moras
						if (disponible > 0) {
							DescuentoDetalle descuentoDetalle = new DescuentoDetalle();
							if (tempMora>0) {
								if (disponible >= tempMora) {
									if (disponible == tempMora) {
										prestamoDetalle.setDescuento_mora(disponible + prestamoDetalle.getDescuento_mora());
										descuentoDetalle.setTotalDescuento(disponible);
										disponible = 0;
									} else {
										prestamoDetalle.setDescuento_mora(prestamoDetalle.getDescuento_mora()+tempMora);
										descuentoDetalle.setTotalDescuento(tempMora);
										disponible -= tempMora;
									}
								} else {
									prestamoDetalle.setDescuento_mora(prestamoDetalle.getDescuento_mora() + disponible);
									descuentoDetalle.setTotalDescuento(disponible);
									disponible = -tempMora;
								}
//								double descuentoMoraFin = disponible;
							}
							if(descuentoDetalle.getTotalDescuento().doubleValue()>0) {
								descuentoDetalle.setCarpeta(carpeta);
								descuentoDetalle.setEmpresa(empresa);
								descuentoDetalle.setFecha(new Date());
								descuentoDetalle.setCliente(prestamo.getCliente());
								descuentoDetalle.setUsuario(usuario);
								descuentoDetalle.setPrestamo(prestamo);
								descuentoDetalle.setConcepto("mora");
								serviceDescuentosDetalles.guardar(descuentoDetalle);
							}
							servicePrestamosDetalles.guardar(prestamoDetalle);
						}
						
					}
					response = 1;
				}
			}else if(tipo == 2) {
				//Adicionales
				PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(cuota);
				List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoDetalle(prestamoDetalle);
			
				double montoTemp = 0;
				
				for (PrestamoAdicional adicional : adicionales) {
					
					DescuentoDetalle descuentoDetalle = new DescuentoDetalle();
					descuentoDetalle.setCarpeta(carpeta);
					descuentoDetalle.setEmpresa(empresa);
					descuentoDetalle.setFecha(new Date());
					descuentoDetalle.setCliente(prestamo.getCliente());
					descuentoDetalle.setUsuario(usuario);
					descuentoDetalle.setPrestamo(prestamo);
					descuentoDetalle.setConcepto("adicionales");
					
					if(adicional!=null) {
						montoTemp += adicional.getMonto()-adicional.getMonto_pagado()-adicional.getDescuento_adicionales();
					}

					if(montoTemp >= monto) {
						double temp = monto;
						adicional.setDescuento_adicionales(adicional.getDescuento_adicionales() + monto);
						descuentoDetalle.setTotalDescuento(monto);
						servicePrestamosAdicionales.guardar(adicional);
						serviceDescuentosDetalles.guardar(descuentoDetalle);

						response = 1;
						monto-=temp;
					}
										
					if(formato2d(adicional.getMonto().doubleValue()) == formato2d(adicional.getDescuento_adicionales()+adicional.getMonto_pagado())) {
						adicional.setEstado(1);
						servicePrestamosAdicionales.guardar(adicional);
					}
				}
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
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
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
				if(adicional!=null) {
					double montoTemp = adicional.getMonto()-adicional.getMonto_pagado()-adicional.getDescuento_adicionales();
					if(montoTemp >= monto) {
						response = 1;
					}
				}
			}
		}else {
			//Cuota
			if(tipo == 1) {
				//Mora
				double moraTemp = 0;
				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					moraTemp += prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada()-prestamoDetalle.getDescuento_mora();
				}
				if(moraTemp >= monto) {
					response = 1;
				}
			}else if(tipo == 2) { 
				//Adicionales
				PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(cuota);
				double montoTemp = 0;
				List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoDetalle(prestamoDetalle);
				for (PrestamoAdicional adicional : adicionales) {
					if(adicional!=null) {
						montoTemp += adicional.getMonto()-adicional.getMonto_pagado()-adicional.getDescuento_adicionales();
					}
				}
				if(montoTemp >= monto) {
					response = 1;
				}
			}
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/guardarCargoCuota/")
	@ResponseBody
	public ResponseEntity<String> guardarCargoCuota(Model model, HttpSession session,
			Integer idPrestamo, Integer motivo, String nota,
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
			prestamoAdicional.setNumeroCuota(prestamoDetalle.getNumero());
		}
		
		MotivoPrestamoAdicional motivoPrestamoAdicional = serviceMotivosPrestamosAdicionales.buscarPorId(motivo);
		
		prestamoAdicional.setNota(nota);
		prestamoAdicional.setMonto(monto);
		prestamoAdicional.setMotivo(motivoPrestamoAdicional);
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
			Integer idPrestamo, Double monto, Integer tipoCuota, HttpServletRequest request, 
            HttpServletResponse response) throws JRException, SQLException {
		Carpeta carpeta = new Carpeta();
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
		}else {
			//Cargamos la principal
			if(session.getAttribute("empresa")!=null) {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
			}
		}
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		monto = 0.0;
		double abonoEfectivo = 0;
		double abonoCheque = 0;
		double abonoDeposito = 0;
		String imagenCheque = null;
		
		Abono abonoGuardado = null;
		
		String imagenDepositoTransferencia = null;
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		for (PagoTemp pagoTemp : pagosTemp) {
			if(pagoTemp.getTipo() == 1) {
				abonoEfectivo+=pagoTemp.getMonto();
			}else if(pagoTemp.getTipo() == 2) {
				//Deposito / Transferencia
				abonoDeposito+=pagoTemp.getMonto();
				imagenDepositoTransferencia = pagoTemp.getImagen();
			}else if(pagoTemp.getTipo() == 3) {
				//Cheque
				abonoCheque+=pagoTemp.getMonto();
				imagenCheque = pagoTemp.getImagen();
			}
			monto += pagoTemp.getMonto();
		}
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Amortizacion> detalles = new LinkedList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		double disponible = monto;
		double totalMora = 0;
		
		if (tipoCuota == 1) {
			if (prestamo.getTipo().equals("2")) {
				//Interes
				Abono abono = new Abono(); 
				List<Abono> abonos = serviceAbonos.buscarPorPrestamo(prestamo);
				int numAbono = 1;
				if(!abonos.isEmpty()) {
					numAbono = abonos.get(abonos.size()-1).getNumero()+1;
				}
				
				//Guardamos el abono en la tabla abonos
				abono.setMonto(monto);
				abono.setCliente(prestamo.getCliente());
				abono.setEstado(0);
				abono.setFecha(new Date());
				abono.setNumero(numAbono);
				abono.setPrestamo(prestamo);
				abono.setUsuario(usuario);
				abono.setEfectivo(abonoEfectivo);
				abono.setCheque(abonoCheque);
				abono.setTransferencia_deposito(abonoDeposito);
				abono.setEmpresa((Empresa) session.getAttribute("empresa"));
				abono.setCarpeta(carpeta);
				if(imagenCheque!=null) {
					abono.setImagen_cheque(imagenCheque);
				}
				if(imagenDepositoTransferencia!=null) {
					abono.setImagen_deposito_transferencia(imagenDepositoTransferencia);
				}
				serviceAbonos.guardar(abono);
				
				abonoGuardado = abono;
				
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
								  double pagadoCargoInicio = disponible;
								  double tempMonto = adicionales.getMonto()-adicionales.getDescuento_adicionales();
//								  pagadoCargoFin = tempMonto;
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
											double pagadoCargoFin = disponible;
											servicePrestamosAdicionales.guardar(adicionales);
											AbonoDetalle abonoDetalle = new AbonoDetalle();
											abonoDetalle.setAbono(abono);
											abonoDetalle.setConcepto("Cargo");
											abonoDetalle.setDetalle(adicionales.getMotivo().getMotivo());
											abonoDetalle.setMonto(pagadoCargoInicio-pagadoCargoFin);
											abonoDetalle.setNumeroCuota(adicionales.getNumeroCuota());
											serviceAbonosDetalles.guardar(abonoDetalle);
										}
									}
								}
							}
						}
						
						double pagadoMoraInicio = disponible;
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
								double pagadoMoraFin = disponible;
								servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
								AbonoDetalle abonoDetalle = new AbonoDetalle();
								abonoDetalle.setAbono(abono);
								abonoDetalle.setConcepto("Mora");
								abonoDetalle.setMonto(pagadoMoraInicio-pagadoMoraFin);
								abonoDetalle.setNumeroCuota(prestamoInteresDetalle.getNumero_cuota());
								serviceAbonosDetalles.guardar(abonoDetalle);
							}
						}
					
						double pagadoInteresInicio = disponible;
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
								double pagadoInteresFin = disponible;
								AbonoDetalle abonoDetalle = new AbonoDetalle();
								abonoDetalle.setAbono(abono);
								abonoDetalle.setConcepto("Interes");
								abonoDetalle.setMonto(pagadoInteresInicio-pagadoInteresFin);
								abonoDetalle.setNumeroCuota(prestamoInteresDetalle.getNumero_cuota());
								serviceAbonosDetalles.guardar(abonoDetalle);
							}
						}
					}
				}
				
				//Antes de pagar el Capital volvemos a verificar los cargos por prestamo solamente
				if (disponible > 0) {
					double pagadoCargoInicio = disponible;
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
									double pagadoCargoFin = disponible;
									AbonoDetalle abonoDetalle = new AbonoDetalle();
									abonoDetalle.setAbono(abono);
									abonoDetalle.setConcepto("Cargo");
									abonoDetalle.setMonto(pagadoCargoInicio-pagadoCargoFin);
									abonoDetalle.setDetalle(adicionalesTemp.getMotivo().getMotivo());
									abonoDetalle.setNumeroCuota(adicionalesTemp.getNumeroCuota());
									serviceAbonosDetalles.guardar(abonoDetalle);
								}
							}
						}
					}
				}
								
//				Pagamos el capital
				if(disponible>0) {
					double pagoCapitalInicio = disponible;
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
					double pagoCapitalFin = disponible;
					AbonoDetalle abonoDetalle = new AbonoDetalle();
					abonoDetalle.setAbono(abono);
					abonoDetalle.setConcepto("Capital");
					abonoDetalle.setMonto(pagoCapitalInicio-pagoCapitalFin);
					serviceAbonosDetalles.guardar(abonoDetalle);
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
//				//Cuotas
				Abono abono = new Abono(); 
				List<Abono> abonos = serviceAbonos.buscarPorPrestamo(prestamo);
				int numAbono = 1;
				if(!abonos.isEmpty()) {
					numAbono = abonos.get(abonos.size()-1).getNumero()+1;
				}
				
				//Guardamos el abono en la tabla abonos
				abono.setMonto(monto);
				abono.setCliente(prestamo.getCliente());
				abono.setEstado(0);
				abono.setFecha(new Date());
				abono.setNumero(numAbono);
				abono.setPrestamo(prestamo);
				abono.setUsuario(usuario);
				abono.setEfectivo(abonoEfectivo);
				abono.setCheque(abonoCheque);
				abono.setTransferencia_deposito(abonoDeposito);
				abono.setEmpresa((Empresa) session.getAttribute("empresa"));
				abono.setCarpeta(carpeta);
				if(imagenCheque!=null) {
					abono.setImagen_cheque(imagenCheque);
				}
				if(imagenDepositoTransferencia!=null) {
					abono.setImagen_deposito_transferencia(imagenDepositoTransferencia);
				}
				serviceAbonos.guardar(abono);
				abonoGuardado = abono;
				
				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles
						.buscarPorPrestamo(prestamo);

				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					if (prestamoDetalle.getEstado().doubleValue() != 1) {
						// Pagamos los cargos que no esten pagados
						List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero())
								.stream().filter(c -> c.getEstado() == 0).collect(Collectors.toList());
						if (disponible > 0) {
							if (!prestamosAdicionales.isEmpty()) {
								for (PrestamoAdicional adicionales : prestamosAdicionales) {
								  double pagadoCargoInicio = disponible;
								  double tempMonto = adicionales.getMonto()-adicionales.getDescuento_adicionales();
//								  pagadoCargoFin = tempMonto;
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
											double pagadoCargoFin = disponible;
											servicePrestamosAdicionales.guardar(adicionales);
											AbonoDetalle abonoDetalle = new AbonoDetalle();
											abonoDetalle.setAbono(abono);
											abonoDetalle.setConcepto("Cargo");
											abonoDetalle.setDetalle(adicionales.getMotivo().getMotivo());
											abonoDetalle.setMonto(pagadoCargoInicio-pagadoCargoFin);
											abonoDetalle.setNumeroCuota(adicionales.getNumeroCuota());
											serviceAbonosDetalles.guardar(abonoDetalle);
										}
									}
								}
							}
						}
						
						double pagadoMoraInicio = disponible;
						double tempMora = prestamoDetalle.getMora()-prestamoDetalle.getDescuento_mora();
						//Pagamos las moras
						if (disponible > 0) {
							if (prestamoDetalle.getMora_pagada() < tempMora) {
								if (disponible + prestamoDetalle.getMora_pagada() >= tempMora) {
									if (disponible + prestamoDetalle.getMora_pagada().doubleValue() == tempMora) {
										prestamoDetalle.setMora_pagada(disponible + prestamoDetalle.getMora_pagada());
										disponible = 0;
									} else {
										double montoTemp = tempMora - prestamoDetalle.getMora_pagada();
										prestamoDetalle.setMora_pagada(tempMora);
										disponible -= montoTemp;
									}
								} else {
									if (prestamoDetalle.getMora_pagada().doubleValue() == tempMora) {
										continue;
									} else {
										prestamoDetalle.setMora_pagada(prestamoDetalle.getMora_pagada() + disponible);
										disponible = 0;
									}
								}
								if (prestamoDetalle.getMora_pagada().doubleValue() == tempMora) {
								}
								double pagadoMoraFin = disponible;
								servicePrestamosDetalles.guardar(prestamoDetalle);
								AbonoDetalle abonoDetalle = new AbonoDetalle();
								abonoDetalle.setAbono(abono);
								abonoDetalle.setConcepto("Mora");
								abonoDetalle.setMonto(pagadoMoraInicio-pagadoMoraFin);
								abonoDetalle.setNumeroCuota(prestamoDetalle.getNumero());
								serviceAbonosDetalles.guardar(abonoDetalle);
							}
						}
					
						double pagadoInteresInicio = disponible;
						//Pagamos el interes
						if(disponible>0) {
							if (prestamoDetalle.getInteres_pagado() < prestamoDetalle
									.getInteres()) {
								if (disponible + prestamoDetalle
										.getInteres_pagado() >= prestamoDetalle.getInteres()) {
									if (disponible + prestamoDetalle.getInteres_pagado()
											.doubleValue() == prestamoDetalle.getInteres().doubleValue()) {
										prestamoDetalle.setInteres_pagado(
												disponible + prestamoDetalle.getInteres_pagado());
										disponible = 0;
									} else {
										double interesTemp = prestamoDetalle.getInteres()
												- prestamoDetalle.getInteres_pagado();
										prestamoDetalle
												.setInteres_pagado(prestamoDetalle.getInteres());
										disponible -= interesTemp;
									}
								} else {
									if (prestamoDetalle.getInteres_pagado()
											.doubleValue() == prestamoDetalle.getInteres().doubleValue()) {
										continue;
									} else {
										prestamoDetalle.setInteres_pagado(
												prestamoDetalle.getInteres_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalle);
								double pagadoInteresFin = disponible;
								AbonoDetalle abonoDetalle = new AbonoDetalle();
								abonoDetalle.setAbono(abono);
								abonoDetalle.setConcepto("Interes");
								abonoDetalle.setMonto(pagadoInteresInicio-pagadoInteresFin);
								abonoDetalle.setNumeroCuota(prestamoDetalle.getNumero());
								serviceAbonosDetalles.guardar(abonoDetalle);
							}
						}
						
//						Pagamos el capital
						double pagadoCapitalInicio = disponible;
						//Pagamos el interes
						if(disponible>0) {
							if (prestamoDetalle.getCapital_pagado() < prestamoDetalle
									.getCapital()) {
								if (disponible + prestamoDetalle
										.getCapital_pagado() >= prestamoDetalle.getCapital()) {
									if (disponible + prestamoDetalle.getCapital_pagado()
											.doubleValue() == prestamoDetalle.getCapital().doubleValue()) {
										prestamoDetalle.setCapital_pagado(
												disponible + prestamoDetalle.getCapital_pagado());
										disponible = 0;
									} else {
										double capitalTemp = prestamoDetalle.getCapital()
												- prestamoDetalle.getCapital_pagado();
										prestamoDetalle
												.setCapital_pagado(prestamoDetalle.getCapital());
										disponible -= capitalTemp;
									}
								} else {
									if (prestamoDetalle.getCapital_pagado()
											.doubleValue() == prestamoDetalle.getCapital().doubleValue()) {
										continue;
									} else {
										prestamoDetalle.setCapital_pagado(
												prestamoDetalle.getCapital_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalle);
								double pagadoCapitalFin = disponible;
								AbonoDetalle abonoDetalle = new AbonoDetalle();
								abonoDetalle.setAbono(abono);
								abonoDetalle.setConcepto("Capital");
								abonoDetalle.setMonto(pagadoCapitalInicio-pagadoCapitalFin);
								abonoDetalle.setNumeroCuota(prestamoDetalle.getNumero());
								serviceAbonosDetalles.guardar(abonoDetalle);
							}
						}
						
					}
				}
				
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					if(
						prestamoDetalle.getCapital_pagado().equals(prestamoDetalle.getCapital()) &&
						prestamoDetalle.getInteres_pagado().equals(prestamoDetalle.getInteres()) && 
						prestamoDetalle.getMora_pagada().doubleValue()+prestamoDetalle.getDescuento_mora().doubleValue() == formato2d(prestamoDetalle.getMora())) {
						prestamoDetalle.setEstado(1);
						prestamoDetalle.setEstado_cuota("Saldo");
						prestamoDetalle.setDias_atraso(0);
						servicePrestamosDetalles.guardar(prestamoDetalle);
					}
				}
			}
		}
		
		//Borramos los pagos temp
		servicePagosTemp.eliminar(pagosTemp);
				
		return new ResponseEntity<>(abonoGuardado.getId().toString(), HttpStatus.OK);
	}

	@GetMapping("/imprimirDetalleAbono/{id}")
	private void imprimirDetalleAbono(@PathVariable(name = "id") Integer id, HttpServletRequest request,
			HttpServletResponse response) throws JRException, SQLException {
		
		Abono abonoGuardado = serviceAbonos.buscarPorId(id);
				
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportAbono);
		
		List<DetalleReporteAbono> detalleReporteAbonos = new LinkedList<>();
		
		double totalCapital = 0.0;

//		Empresa empresa = abonoGuardado.getEmpresa();
		
		Empresa empresa = serviceEmpresas.buscarPorId(1);
		
		List<AbonoDetalle> abonosDetalles = serviceAbonosDetalles.buscarPorAbonoOrderByCuota(abonoGuardado);
		
		List<AbonoDetalle> abonosDetallesTemp = new LinkedList<>();
		
		List<DetalleReporteAbono> reporteAbonoCapital = new LinkedList<>();
		
		for (AbonoDetalle abonoDetalle : abonosDetalles) {
			
			double registroCapital = 0;
			double registroInteres = 0;
			double registroCargo = 0;
			double registroMora = 0;
			
			if(abonosDetallesTemp.isEmpty()) {
				abonosDetallesTemp.add(abonoDetalle);
			}
			
			List<DetalleReporteAbono> detalleReporteAbonosTemp = detalleReporteAbonos.stream().
										filter(d -> d.getCuota() == abonoDetalle.getNumeroCuota()).
			collect(Collectors.toList());
			
			if(detalleReporteAbonosTemp.isEmpty()) {				
				if(abonoDetalle.getConcepto().equalsIgnoreCase("Cargo")) {
					registroCargo = abonoDetalle.getMonto();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Mora")) {
					registroMora = abonoDetalle.getMonto();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Interes")) {
					registroInteres = abonoDetalle.getMonto();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Capital")) {
					registroCapital = abonoDetalle.getMonto();
				}
								
				DetalleReporteAbono detalleReporteAbono = new DetalleReporteAbono();
				detalleReporteAbono.setPagoCargos(formato2d(registroCargo));
				detalleReporteAbono.setPagoInteres(formato2d(registroInteres));
				detalleReporteAbono.setPagoCapital(formato2d(registroCapital));
				detalleReporteAbono.setPagoMoras(formato2d(registroMora));
				detalleReporteAbono.setCuota(abonoDetalle.getNumeroCuota());
				detalleReporteAbono.setId(detalleReporteAbono.getId());

				detalleReporteAbonos.add(detalleReporteAbono);
			}else {
				//Si existe actualiza
				if(abonoDetalle.getConcepto().equalsIgnoreCase("Cargo")) {
					registroCargo = detalleReporteAbonosTemp.get(0).getPagoCargos().doubleValue() + abonoDetalle.getMonto().doubleValue();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Mora")) {
					registroMora = detalleReporteAbonosTemp.get(0).getPagoMoras().doubleValue() + abonoDetalle.getMonto().doubleValue();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Interes")) {
					registroInteres = detalleReporteAbonosTemp.get(0).getPagoInteres().doubleValue() + abonoDetalle.getMonto().doubleValue();
				}else if(abonoDetalle.getConcepto().equalsIgnoreCase("Capital")) {
					registroCapital =  formato2d(detalleReporteAbonosTemp.get(0).getPagoCapital().doubleValue() + abonoDetalle.getMonto());
				}
				
				detalleReporteAbonosTemp.get(0).setPagoCapital(formato2d(detalleReporteAbonosTemp.get(0).getPagoCapital()+registroCapital));
				detalleReporteAbonosTemp.get(0).setPagoCargos(formato2d(detalleReporteAbonosTemp.get(0).getPagoCargos() + registroCargo));
				detalleReporteAbonosTemp.get(0).setPagoInteres(formato2d(detalleReporteAbonosTemp.get(0).getPagoInteres() + registroInteres));
				detalleReporteAbonosTemp.get(0).setPagoMoras(formato2d(detalleReporteAbonosTemp.get(0).getPagoMoras() + registroMora));
			}
		}
		
		double balance = 0;
		
		for (DetalleReporteAbono abonoDetalle : detalleReporteAbonos) {
			double total = formato2d(abonoDetalle.getPagoCapital()+abonoDetalle.getPagoCargos()+abonoDetalle.getPagoInteres()+abonoDetalle.getPagoMoras());
			abonoDetalle.setTotal(total);
			
			if(abonoGuardado.getPrestamo().getTipo().equalsIgnoreCase("2")) {
				reporteAbonoCapital = detalleReporteAbonos.stream().filter(a -> a.getPagoCapital().doubleValue()>0).collect(Collectors.toList());
				detalleReporteAbonos = detalleReporteAbonos.stream().filter(a -> a.getPagoCapital().doubleValue()==0).collect(Collectors.toList());
				for (DetalleReporteAbono detalleReporteAbono : reporteAbonoCapital) {
					totalCapital+=detalleReporteAbono.getPagoCapital();
				}
				//Interes
//				PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorPrestamo(abonoGuardado.getPrestamo()).stream().filter(p -> p.getNumero_cuota() == abonoDetalle.getCuota()).collect(Collectors.toList()).get(0);
//				abonoDetalle.setTipo(prestamoInteresDetalle.getEstado()==1?"Saldo":"Abono");
			}else {
				//Cuotas
				PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorPrestamo(abonoGuardado.getPrestamo()).stream().filter(p -> p.getNumero() == abonoDetalle.getCuota()).collect(Collectors.toList()).get(0);
				abonoDetalle.setTipo(prestamoDetalle.getEstado_cuota().equalsIgnoreCase("Saldo")?"Saldo":"Abono");
			}
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(detalleReporteAbonos);
		
		Numero_Letras numeroLetras = new Numero_Letras();
		
		parameters.put("idEmpresa", empresa.getId()); 
		parameters.put("cliente", abonoGuardado.getCliente().getNombre()); 
		parameters.put("direccionCliente", abonoGuardado.getCliente().getDireccion());
		parameters.put("cedulaCliente", abonoGuardado.getCliente().getCedula());
		parameters.put("fecha", formatter.format(abonoGuardado.getFecha()));
		parameters.put("numeroAbono", abonoGuardado.getNumero().toString());
		parameters.put("prestamo", abonoGuardado.getPrestamo().getCodigo().toString());
		parameters.put("imagen", ruta+empresa.getLogo());
		parameters.put("totalPagado", abonoGuardado.getMonto());
		parameters.put("nombreUsuario", abonoGuardado.getUsuario().getNombre());
		parameters.put("totalCapital", totalCapital);
		parameters.put("balance", balance);
		
		parameters.put("monto", numeroLetras.Convertir(abonoGuardado.getMonto().toString(), true) + " " + abonoGuardado.getMonto());
		parameters.put("detalleReporteAbonos", itemsJRBean);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
		
		String dataDirectory = tempFolder + pathSeparator + "abono"+abonoGuardado.getId()+".pdf";
		
		tempFolder += pathSeparator;
		
		JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
	
		 //If user is not authorized - he should be thrown out from here itself
         
        //Authorized user will download the file
//        String dataDirectory = request.getServletContext().getRealPath("/WEB-INF/downloads/pdf/");
		
		//Descarga
//        Path file = Paths.get(tempFolder, "abono"+abonoGuardado.getId()+".pdf");
//        if (Files.exists(file)) 
//        {
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "attachment; filename="+"abono"+abonoGuardado.getId()+".pdf");
//            try
//            {
//                Files.copy(file, response.getOutputStream());
//                response.getOutputStream().flush();
//            } 
//            catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
        
		//PreImpresion
        Path file = Paths.get(tempFolder, "abono"+abonoGuardado.getId()+".pdf");
        if (Files.exists(file)) 
        {
            String mimeType = URLConnection.guessContentTypeFromName(tempFolder+"abono"+abonoGuardado.getId()+".pdf");
            if (mimeType == null) mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "abono"+abonoGuardado.getId()+".pdf" + "\""));
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
				Files.delete(file);
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
		
	}

	@GetMapping("/prestamosPendientes")
	public String prestamosPendientes(Model model, HttpSession session) {
		Carpeta carpeta = new Carpeta();
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
		}else {
			//Cargamos la principal
			if(session.getAttribute("empresa")!=null) {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
			}
		}
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpeta(carpeta).stream().
				filter(p -> p.getEstado() != 1).collect(Collectors.toList());
		
		for (Prestamo prestamo : prestamos) {
			if(prestamo.getTipo().equals("2")) {
				//Interes
				List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoInteresDetalle interesDetalle : prestamoInteresDetalles) {
					if(interesDetalle.getEstado() == 3) {
						prestamo.setEstado(3);
						break;
					}
					
					if(interesDetalle.getEstado() == 2) {
						prestamo.setEstado(2);
						break;
					}
					
					if(interesDetalle.getEstado() == 0) {
						prestamo.setEstado(0);
						break;
					}
				}
			}else {
				//Cuotas
				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoDetalle detalle : prestamoDetalles) {
					if(detalle.getEstado_cuota().equalsIgnoreCase("Legal")) {
						prestamo.setEstado(3);
						break;
					}
					
					if(detalle.getEstado_cuota().equalsIgnoreCase("Atraso")) {
						prestamo.setEstado(2);
						break;
					}
					
					if(detalle.getEstado_cuota().equalsIgnoreCase("Normal")) {
						prestamo.setEstado(0);
						break;
					}
				}
			}
		}
		
		if(prestamos!=null) {
			LocalDateTime fechaAcct =  LocalDateTime.now();
			for (Prestamo prestamosTemp : prestamos) {
				List<Nota> notas = serviceNotas.buscarPorPrestamo(prestamosTemp);
				if(!notas.isEmpty()) {
					int count = 0;
					for (Nota nota : notas) {
						LocalDateTime fechaNota = convertToLocalDateTimeViaInstant(nota.getFecha());
						if(fechaNota.isAfter(fechaAcct) || fechaNota.isEqual(fechaAcct)) {
							count++;
						}
					}
					prestamosTemp.setNumeroNota(count);
				}else {
					prestamosTemp.setNumeroNota(0);
				}
			}
		}
		
		model.addAttribute("moneda", "");
		model.addAttribute("item", "");
		model.addAttribute("estado", "");
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("prestamos", prestamos);
		return "prestamos/prestamosPendientes :: prestamosPendientes";
	}
	
	@GetMapping("/cedulaClienteCobros/{id}")
	public String cedulaClienteCobros(Model model, @PathVariable("id") Integer idCliente) {
		Cliente cliente = serviceClientes.buscarPorId(idCliente);
		model.addAttribute("cliente", cliente);
		return "prestamos/prestamosPendientes :: #detalleCedulaCliente";
	}
	
	@PostMapping("/prestamosPendientesFiltro")
	public String prestamosPendientesFiltro(Model model, HttpSession session,
			@RequestParam(required = false, name = "carpetaId") Integer carpetaId, 
			@RequestParam(required = false, name = "estado") String estado,
			@RequestParam(required = false, name = "item") String item,
			@RequestParam(required = false, name = "moneda") String moneda) {
		Carpeta carpeta = new Carpeta();
		
		if(estado == null) {
			estado = "";
		}
		
		if(carpetaId == null) {
			
			if(session.getAttribute("carpeta")!=null) {
				carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
			}else {
				// Cargamos la principal
				List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
				if (!carpetas.isEmpty()) {
					carpeta = carpetas.get(0);
				}
			}
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
//		Cliente cliente = new Cliente();
//		
//		if(session.getAttribute("cliente") == null) {
//			 cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
//		}
		
		List<Prestamo> prestamos  = servicePrestamos.buscarPorCarpeta(carpeta).stream().
				filter(p -> p.getEstado() != 1).collect(Collectors.toList());
		
		if(!prestamos.isEmpty()) {
			for (Prestamo prestamo : prestamos) {
				if(prestamo.getTipo().equals("2")) {
					//Interes
					List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
					for (PrestamoInteresDetalle interesDetalle : prestamoInteresDetalles) {
						if(interesDetalle.getEstado() == 3) {
							prestamo.setEstado(3);
							break;
						}
						
						if(interesDetalle.getEstado() == 2) {
							prestamo.setEstado(2);
							break;
						}
						
						if(interesDetalle.getEstado() == 0) {
							prestamo.setEstado(0);
							break;
						}
					}
				}else {
					//Cuotas
					List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
					for (PrestamoDetalle detalle : prestamoDetalles) {
						if(detalle.getEstado_cuota().equalsIgnoreCase("Legal")) {
							prestamo.setEstado(3);
							break;
						}
						
						if(detalle.getEstado_cuota().equalsIgnoreCase("Atraso")) {
							prestamo.setEstado(2);
							break;
						}
						
						if(detalle.getEstado_cuota().equalsIgnoreCase("Normal")) {
							prestamo.setEstado(0);
							break;
						}
					}
				}
			}
		}
		
		List<Prestamo> filterList = new LinkedList<>();
		
		if(estado.equalsIgnoreCase("2")) {
			filterList = prestamos.stream().
					filter(p -> p.getEstado() == 2).collect(Collectors.toList());
		}else if(estado.equalsIgnoreCase("3")) {
			filterList  = prestamos.stream().
					filter(p -> p.getEstado() == 3).collect(Collectors.toList());
		}else if(estado.equalsIgnoreCase("0")) {
			filterList  = prestamos.stream().
					filter(p -> p.getEstado() == 0).collect(Collectors.toList());
		}else {
			filterList = prestamos;
		}
		
		if(item != null) {
			if(!item.equals("0")) {
				filterList = filterList.stream().filter( p -> 
				p.getCliente().getNombre().toLowerCase().contains(item.toLowerCase()) || 
				p.getCliente().getCedula().toLowerCase().contains(item.toLowerCase())
						).collect(Collectors.toList());
			}
		}
		
		if(filterList!=null) {
			LocalDateTime fechaAcct =  LocalDateTime.now();
			for (Prestamo prestamosTemp : filterList) {
				List<Nota> notas = serviceNotas.buscarPorPrestamo(prestamosTemp);
				if(!notas.isEmpty()) {
					int count = 0;
					for (Nota nota : notas) {
						LocalDateTime fechaNota = convertToLocalDateTimeViaInstant(nota.getFecha());
						if(fechaNota.isAfter(fechaAcct) || fechaNota.isEqual(fechaAcct)) {
							count++;
						}
					}
					prestamosTemp.setNumeroNota(count);
				}else {
					prestamosTemp.setNumeroNota(0);
				}
			}
		}
		
		if(moneda!="") {
			filterList = filterList.stream().filter( p -> 
			p.getMoneda().equalsIgnoreCase(moneda)
					).collect(Collectors.toList());
		}

		model.addAttribute("prestamos", filterList);
		return "prestamos/prestamosPendientes :: #tablaPrestamosCobros";
	}
	
	@GetMapping("/verNotas/{id}")
	public String verNotas(@PathVariable(name = "id") Integer id, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		LocalDateTime fechaAcct =  LocalDateTime.now();
		//Notas asociadas al prestamo
		List<Nota> notas = serviceNotas.buscarPorPrestamo(prestamo);
		List<Nota> depuredNota = new LinkedList<>();
		for (Nota nota : notas) {
			LocalDateTime fechaNota = convertToLocalDateTimeViaInstant(nota.getFecha());
			if(fechaNota.isAfter(fechaAcct) || fechaNota.isEqual(fechaAcct)) {
				depuredNota.add(nota);
			}
		}
		model.addAttribute("notas", depuredNota);
		model.addAttribute("prestamo", prestamo);
		return "notas/infoNotas :: infoNotas";
	}
	
	@PostMapping("/crearPagoTemp")
	public String crearPagoTemp(Abono abono, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(abono.getId());
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		PagoTemp pagoTemp = new PagoTemp();
		
		if (!abono.getImagen().isEmpty()) {
			String nombreImagen = Utileria.guardarArchivo(abono.getImagen(), ruta);
			if (nombreImagen != null) {
				abono.setNombreImagen(nombreImagen);
			}
		}
		
		pagoTemp.setMonto(abono.getMonto());
		pagoTemp.setTipo(abono.getTipoPago());
		pagoTemp.setPrestamo(prestamo);
		pagoTemp.setImagen(abono.getNombreImagen());
		servicePagosTemp.guardar(pagoTemp);
		pagosTemp.add(pagoTemp);
		model.addAttribute("pagoTemp", pagosTemp);
		return "notas/infoNotas :: #tablaPagos";
	}
	
	@GetMapping("/cargarPagoTemp/{id}")
	public String cargarPagoTemp(@PathVariable("id") Integer idPrestamo, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		model.addAttribute("pagoTemp", pagosTemp);
		return "notas/infoNotas :: #tablaPagos";
	}
	
	@GetMapping("/montoTotalAbono/{id}")
	public String montoTotalAbono(@PathVariable("id") Integer idPrestamo, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		double montoTotalAbono = 0;
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		for (PagoTemp pagoTemp : pagosTemp) {
			montoTotalAbono += pagoTemp.getMonto();
		}
		model.addAttribute("montoTotalAbono", montoTotalAbono);
		return "notas/infoNotas :: #montoTotalAbono";
	}
	
	@GetMapping("/eliminarPagoTemp/{id}")
	public String eliminarPagoTemp(@PathVariable("id") Integer pagoTempId, Model model) {
		PagoTemp pagoTemp = servicePagosTemp.buscarPorId(pagoTempId);
		Prestamo prestamo = pagoTemp.getPrestamo();
		if(!(pagoTemp.getImagen()==null)) {
			File imageFile = new File(ruta+ pagoTemp.getImagen());
			imageFile.delete();
		}
		servicePagosTemp.eliminar(pagoTemp);
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		model.addAttribute("pagoTemp", pagosTemp);
		return "notas/infoNotas :: #tablaPagos";
	}
	
	@GetMapping("/eliminarPagosTemp/{id}")
	@ResponseBody
	public ResponseEntity<String> eliminarPagosTemp(@PathVariable("id") Integer prestamoId) {
		Prestamo prestamo = servicePrestamos.buscarPorId(prestamoId);
		List<PagoTemp> pagosTemp = servicePagosTemp.buscarPorPrestamo(prestamo);
		for (PagoTemp pagoTemp : pagosTemp) {
			if(pagoTemp.getImagen() == null || pagoTemp.getImagen().equals("")) {
				File imageFile = new File(ruta + pagoTemp.getImagen());
				imageFile.delete();
			}
		}
		servicePagosTemp.eliminar(pagosTemp);
		return new ResponseEntity<>(HttpStatus.OK);
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
