package com.contable.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;

@Component
public class PrestamosCron {

	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	private final Integer NORMAL = 0;
//	private final Integer PAGADO = 1;
	private final Integer VENCIDO = 2;

	@Scheduled(cron = "0 29 14 * * *")
	public void calculoVencimientoCuota() throws ParseException {
		//Buscamos los detalles vencidos de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(2);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				//Le sumamos los dias de gracia
				LocalDateTime fechaMax = fecha.plusDays(prestamoDetalle.getPrestamo().getDias_gracia());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fechaAcct.isAfter(fechaMax) || fechaAcct.isEqual(fechaMax)) {
					//valor_cuota x valor_interes_mora divido/30 x dias_vencidos
					double vencidos = diasVencidos(fecha, fechaAcct);
					//Calculamos los dias vencidos despues de los dias de gracia
					Double mora = ((prestamoDetalle.getCuota() * (prestamoDetalle.getInteres_mora()/100) ) / 30.00) * vencidos;
					prestamoDetalle.setMora(formato2d(mora));
					prestamoDetalle.setDias_atraso((int) vencidos);
					if(vencidos < 90) {
						prestamoDetalle.setEstado_cuota("Atraso");
					}else {
						prestamoDetalle.setEstado_cuota("Legal");
					}
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 28 14 * * *")
	public void diasVencidos() throws ParseException {
		//Buscamos los detalles pendientes de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(0);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fecha.isBefore(fechaAcct) || fecha.isEqual(fechaAcct)) {
					prestamoDetalle.setEstado(2);
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 30 14 * * *")
	public void generarPrestamoInteresDetalle() throws ParseException {
		List<Prestamo> prestamos = servicePrestamos.buscarPorEstado(NORMAL);
		LocalDateTime dateAcct =  LocalDateTime.now();
		LocalDateTime fechaCron = null;

		for (Prestamo prestamo : prestamos) {
			int count = 0;
			prestamo = servicePrestamos.buscarPorId(prestamo.getId());
			if(prestamo.getEstado()!=1) {
				fechaCron = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron());
				while (fechaCron.isBefore(dateAcct)) {
					fechaCron = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron());
					if (fechaCron.isBefore(dateAcct)) {
						double monto = prestamo.getBalance() * (prestamo.getTasa() / 100);
						PrestamoInteresDetalle prestamoInteresDetalle = new PrestamoInteresDetalle();
						count++;
						
						prestamoInteresDetalle.setInteres(monto);
						prestamoInteresDetalle.setTasa(prestamo.getMora());

						LocalDateTime fechaCuotaTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1).minusDays(prestamo.getDias_gracia());
						Date fechaCuota = convertToDateViaInstant(fechaCuotaTemp);

						prestamoInteresDetalle.setFecha_cuota(fechaCuota);
						prestamoInteresDetalle.setFecha(new Date());

						LocalDateTime fechaVencimientoTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1);
						Date fechaVencimiento = convertToDateViaInstant(fechaVencimientoTemp);

						prestamoInteresDetalle.setVencimiento(fechaVencimiento);
						prestamoInteresDetalle.setPrestamo(prestamo);
						prestamoInteresDetalle.setNumero_cuota(count);
						
						servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);

						// Actualizamos la fecha cron del prestamo a 1 mes
						LocalDateTime fechaCronPrestamoTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1);
						Date fechaCronPrestamo = convertToDateViaInstant(fechaCronPrestamoTemp);
						prestamo.setFecha_cron(fechaCronPrestamo);
						servicePrestamos.guardar(prestamo);
					}
				}
			}
		}
		
		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorEstado(NORMAL);
		
		for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
			fechaCron = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getPrestamo().getFecha_cron());
			Date fecha = prestamoInteresDetalle.getFecha_cuota();
			
			if(fechaCron.isAfter(dateAcct)) {

				LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(fecha);
				double vencidos = diasVencidos(fechaTemp, dateAcct);
				
				//Calculamos los dias vencidos despues de los dias de gracia
				if(vencidos<0.0) {
					vencidos = 0;
				}
				
				prestamoInteresDetalle.setDias_atraso((int) vencidos);
				
				if(vencidos>0) {
					Double mora = ((prestamoInteresDetalle.getInteres() * (prestamoInteresDetalle.getPrestamo().getMora()/100) ) / 30.00) * vencidos;
					prestamoInteresDetalle.setMora(mora);
					prestamoInteresDetalle.setEstado(VENCIDO);
				}

				servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
			}
		}
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
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
}
