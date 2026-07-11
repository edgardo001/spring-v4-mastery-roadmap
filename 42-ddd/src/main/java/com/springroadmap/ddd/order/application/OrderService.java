package com.springroadmap.ddd.order.application;

import com.springroadmap.ddd.order.domain.Money;
import com.springroadmap.ddd.order.domain.Order;
import com.springroadmap.ddd.order.domain.OrderId;
import com.springroadmap.ddd.order.domain.OrderItem;
import com.springroadmap.ddd.order.infrastructure.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OrderService - Application Service del bounded context "order".
 *
 * ANALOGIA: como el cajero de un restaurante. No cocina (eso lo hace la cocina = domain),
 * pero orquesta la orden: recibe el pedido, lo registra, coordina el flujo.
 *
 * RESPONSABILIDADES del Application Service (segun DDD):
 *  - Coordinar el flujo de trabajo (una transaccion).
 *  - Cargar / persistir aggregates via el repositorio.
 *  - Delegar la LOGICA DE NEGOCIO al aggregate (aqui: Order.approve()).
 *  - Publicar Domain Events tras persistir (aqui simulado con logging).
 *  - NO contener reglas de negocio propias (esas viven en el dominio).
 *
 * INYECCION POR CONSTRUCTOR (obligatoria en el roadmap):
 *  - Ventaja: campos {@code final}, obligan a proveer las dependencias, facil testear.
 *  - Antes se usaba @Autowired en campos; hoy es mala practica (dificulta tests unitarios).
 */
@Service
public class OrderService {

    // final: garantiza que la referencia no cambia despues del constructor.
    private final OrderRepository repository;

    /**
     * Spring inyecta OrderRepository aqui automaticamente al arrancar.
     * No necesitamos @Autowired porque hay un solo constructor.
     */
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea una nueva Order: calcula el total sumando los subtotales de cada item,
     * construye el aggregate y lo persiste.
     *
     * @param customer nombre del cliente.
     * @param items lineas de la orden.
     * @return la Order recien creada (con OrderId generado).
     */
    @Transactional
    public Order create(String customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Una Order requiere al menos 1 item");
        }
        // Calculamos el total sumando los subtotales. Usamos la moneda del primer item como base.
        Money total = items.get(0).subtotal();
        for (int i = 1; i < items.size(); i++) {
            total = total.add(items.get(i).subtotal());
        }
        Order order = new Order(OrderId.newId(), customer, items, total);
        return repository.save(order);
    }

    /**
     * Aprueba una orden existente delegando la logica al aggregate.
     * Nota importante: el service NO decide si la orden se puede aprobar;
     * simplemente llama a {@code order.approve()} y el aggregate valida sus invariantes.
     */
    @Transactional
    public Order approve(OrderId id) {
        // Optional.orElseThrow: si no hay valor, lanza la excepcion del proveedor.
        Order order = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order no encontrada: " + id.value()));
        order.approve();
        Order saved = repository.save(order);
        // Simulacion de publicacion de eventos: en un sistema real usarias ApplicationEventPublisher.
        saved.getDomainEvents().forEach(evt -> System.out.println("[DomainEvent] " + evt));
        saved.clearDomainEvents();
        return saved;
    }
}
