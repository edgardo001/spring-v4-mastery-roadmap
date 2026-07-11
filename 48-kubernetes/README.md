## 48 — Orquestación en Kubernetes (K8s) para Spring Boot

### Propósito
Aprender a desplegar una aplicación Spring Boot dentro de un clúster de Kubernetes, configurando de manera nativa los "Health Checks" (Liveness y Readiness Probes) y consumiendo variables de entorno de forma segura a través de ConfigMaps y Secrets.

### Problema que resuelve
En el Módulo 26 aprendimos a empaquetar la app en un contenedor Docker. Pero un contenedor aislado no es un sistema de producción.
- ¿Qué pasa si el contenedor muere por un `OutOfMemoryError` en la madrugada?
- ¿Cómo distribuyo el tráfico si quiero correr 10 instancias (réplicas) de mi contenedor de "Ventas" simultáneamente?
- ¿Dónde guardo la contraseña de la Base de Datos para que no esté hardcodeada en el archivo `application.yml`?

### Cómo lo resuelve
**Kubernetes (K8s)** es un orquestador de contenedores. 
1. Le dices a K8s: *"Quiero que SIEMPRE existan 3 contenedores de Ventas"*. Si uno muere a las 3:00 AM, K8s crea uno nuevo automáticamente en segundos (Self-Healing).
2. K8s incluye su propio "Eureka" (Service Discovery) interno. Agrupa los 3 contenedores bajo un nombre DNS interno `http://ventas-service:8080` y balancea la carga entre ellos.
3. Almacena las contraseñas en objetos encriptados (`Secrets`) e inyecta esos valores como variables de entorno directamente en Spring Boot antes de arrancarlo.

### Por qué aprenderlo
Si Docker revolucionó cómo empaquetamos software, Kubernetes revolucionó cómo lo operamos. Hoy en día, es el estándar absoluto (AWS EKS, Google GKE, Azure AKS) para alojar microservicios corporativos. Dominar los descriptores YAML de K8s y las configuraciones de Spring Actuator para K8s es fundamental.

```mermaid
graph TD
    subgraph Clúster de Kubernetes
        IG["Ingress (El Gateway)"] --> SVC["Service (Load Balancer Interno)"]
        
        SVC --> P1["Pod 1 (Spring Boot)"]
        SVC --> P2["Pod 2 (Spring Boot)"]
        SVC --> P3["Pod 3 (Spring Boot)"]
        
        CM["ConfigMap<br/>(URL de BD)"] -.->|Inyecta| P1
        CM -.->|Inyecta| P2
        CM -.->|Inyecta| P3
        
        SEC["Secret<br/>(Password BD)"] -.->|Inyecta| P1
        SEC -.->|Inyecta| P2
        SEC -.->|Inyecta| P3
    end
    
    U["Usuario Web"] -->|HTTPS| IG

    style IG fill:#fcc419,color:#000
    style SVC fill:#339af0,color:#fff
    style P1 fill:#51cf66,color:#fff
```

---

### Glosario Básico

#### `Pod`
La unidad de despliegue más pequeña en K8s. Es el "envoltorio" que corre tu contenedor de Spring Boot.

#### `Deployment`
Un archivo YAML donde describes cuántas réplicas (Pods) quieres correr, qué imagen de Docker usar y cuánta RAM/CPU le asignas. K8s vigila este Deployment eternamente.

#### `Service`
Como los Pods nacen y mueren (cambiando de IP cada vez), el Service es una IP estática interna (y un nombre DNS) que enruta el tráfico hacia los Pods vivos de forma balanceada. (Hace que Eureka sea innecesario en K8s).

#### `Probes` (Sondas de Salud)
- **Liveness Probe**: K8s te pregunta "¿Estás vivo?". Si respondes que no (HTTP 500), K8s reinicia el contenedor (mata el proceso).
- **Readiness Probe**: K8s te pregunta "¿Estás listo para recibir tráfico HTTP?". Si respondes que no, K8s no te enviará peticiones de usuarios, pero no te mata (ideal mientras Spring Boot arranca y conecta a la BD).

---

### Conceptos

#### 1. Configurando Spring Boot para Kubernetes (Actuator)
- **Qué es** — Spring Boot sabe si se está ejecutando dentro de K8s. Expone automáticamente URLs específicas para los Probes.
- **Código** — (En `application.yml`):
  ```yaml
  management:
    endpoint:
      health:
        probes:
          enabled: true # Activa /actuator/health/liveness y /readiness
  ```
  Al compilar y ejecutar, Spring tendrá dos endpoints listos para que K8s los consulte periódicamente.

#### 2. Escribiendo el Descriptor YAML (El Deployment)
- **Qué es** — El archivo que le entregamos a K8s para que orqueste la app.
- **Código** — `k8s-deployment.yml`:
  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: spring-boot-app
  spec:
    replicas: 3
    selector:
      matchLabels:
        app: spring-boot-app
    template:
      metadata:
        labels:
          app: spring-boot-app
      spec:
        containers:
        - name: spring-app
          image: mi-empresa/spring-boot-app:1.0.0
          ports:
          - containerPort: 8080
          # Configuración Vital: Le dice a K8s cómo vigilar a Spring Boot
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 15 # Dale tiempo a que arranque el contexto de Spring
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 5
  ```

#### 3. Inyección de Configuración (ConfigMap y Secrets)
- **Qué es** — En vez de tener 3 archivos `application.yml` (dev, qa, prod), tienes un solo JAR, y le inyectas las variables desde K8s.
- **Código K8s**:
  ```yaml
  apiVersion: v1
  kind: ConfigMap
  metadata:
    name: spring-config
  data:
    DB_URL: "jdbc:postgresql://mi-bd-prod:5432/ventas"
  ```
- **Código K8s (En el Deployment inyectando el valor)**:
  ```yaml
          env:
          - name: SPRING_DATASOURCE_URL
            valueFrom:
              configMapKeyRef:
                name: spring-config
                key: DB_URL
  ```
  *(Nota de Spring: Si la variable de entorno se llama `SPRING_DATASOURCE_URL`, Spring Boot mágicamente sobreescribe la propiedad `spring.datasource.url` del application.yml).*

#### 4. K8s Service (Reemplazando a Eureka)
- **Qué es** — Crea el balanceador de carga interno.
- **Código**:
  ```yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: spring-boot-service # ESTE SERÁ EL NOMBRE DNS
  spec:
    selector:
      app: spring-boot-app
    ports:
      - protocol: TCP
        port: 80
        targetPort: 8080
  ```
  Si otro microservicio tuyo dentro del clúster hace `restClient.get().uri("http://spring-boot-service/api")`, K8s lo ruteará perfectamente a uno de los 3 pods.

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `CrashLoopBackOff` | K8s reinicia el contenedor infinitamente porque el `LivenessProbe` falló. | Pusiste el `initialDelaySeconds` en 5 segundos. Spring Boot tarda 12 segundos en arrancar. K8s asume que el contenedor está defectuoso y lo mata antes de que termine de iniciar. Aumenta el delay. (O mejor aún, usa una `startupProbe`). |
| OOMKilled (Out Of Memory) | Spring Boot asume que tiene toda la RAM del nodo K8s, reserva el 25% de 16GB, y excede el límite impuesto por el YAML de K8s. | K8s aniquila los pods que exceden sus límites (`resources.limits.memory`). Configura JVM options en el Dockerfile: `-XX:MaxRAMPercentage=75.0` para que Java sea consciente de que corre en un contenedor pequeño. |
| Logs perdidos al reiniciar el Pod | Los logs se estaban guardando en un archivo local (ej. `/app/logs/spring.log`). K8s borró el Pod y creó uno nuevo en otro nodo. El archivo desapareció. | En K8s, **jamás debes loggear a un archivo de disco**. Loggea siempre y exclusivamente hacia la Consola (STDOUT). K8s captura la consola y tú usas ELK/Loki (Módulo 45) para consultarlo. |

---

### Ejercicios
1. Asegúrate de tener Kubernetes activado en Docker Desktop, o usa `minikube`.
2. Habilita los probes de Actuator en tu `application.yml` y compila una imagen Docker de tu proyecto (`docker build -t app-k8s:v1 .`).
3. Crea un archivo `deployment.yaml` con las instrucciones del concepto #2.
4. Aplica el archivo a tu clúster local: `kubectl apply -f deployment.yaml`.
5. Verifica cómo K8s crea los pods con `kubectl get pods`. Haz un `kubectl port-forward svc/spring-boot-service 8080:80` y visita la app en tu navegador. 

### Cómo ejecutar
```bash
cd 48-kubernetes
# Compilar imagen local (Para que K8s la encuentre)
docker build -t mi-spring-k8s:1.0.0 .

# Aplicar manifiestos
kubectl apply -f k8s/configmap.yml
kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml

# Ver el estado mágico de autocuración
kubectl get pods -w
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Configuración de Actuator (`spring-boot-starter-actuator`). |
| `k8s/deployment.yml` | Despliegue de los Pods y configuración de Probes. |
| `k8s/configmap.yml` | Inyección de propiedades como Variables de Entorno. |
| `k8s/service.yml` | Balanceador de Carga nativo de Kubernetes. |

---

### Antes vs Ahora

| Aspecto | Bare metal (JAR) | Docker (mod. 26) | Kubernetes (mod. 48) |
|---------|------------------|------------------|----------------------|
| Empaquetado | Fat-JAR local | Imagen Docker | Imagen Docker + manifiestos YAML |
| Ejecucion | `java -jar` a mano | `docker run` a mano | `kubectl apply -f k8s/` |
| Si el proceso muere | Nada. Alguien lo reinicia manualmente. | Nada (a menos que uses `--restart`). | El Deployment vuelve a crear el Pod automaticamente. |
| Escalar a N replicas | Copiar/pegar JAR en N maquinas. | Correr N contenedores + balanceador externo. | `replicas: N` en el Deployment. |
| Health checks | Ninguno estandar. | Solo "el proceso corre". | `livenessProbe` + `readinessProbe` HTTP contra Actuator. |
| Config por entorno | Editar `application.yml` y redesplegar. | Variables `-e` en `docker run`. | ConfigMap + Secret inyectados como env vars. |
| Networking entre servicios | IP + puerto hardcodeados. | Red Docker + nombres de contenedor. | Service (DNS interno) reemplaza a Eureka. |
| Exposicion externa | Puerto abierto en firewall. | `-p 8080:8080`. | Ingress con hostname (`demo.local`). |

---

### Comandos de despliegue

#### 1. Compilar y construir imagen

```bash
# Compila el fat-JAR (target/kubernetes-1.0.0.jar)
./build.sh          # Linux/Mac
.\build.ps1         # Windows PowerShell

# Construye la imagen Docker local
docker build -t kubernetes-demo:1.0.0 .
```

> Si usas Docker Desktop con Kubernetes activado, la imagen local es visible para el cluster.
> Si usas `minikube`, primero: `eval $(minikube docker-env)` antes de `docker build`.

#### 2. Aplicar manifiestos K8s planos

```bash
kubectl apply -f k8s/
# equivalente a:
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml

# Verificar
kubectl get pods -w
kubectl get svc
kubectl describe deployment kubernetes-demo

# Probar sin ingress
kubectl port-forward svc/kubernetes-demo-service 8080:80
curl http://localhost:8080/api/hello        # -> "Hello from K8s pod"
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness

# Limpiar
kubectl delete -f k8s/
```

#### 3. Alternativa con Helm

```bash
helm install kubernetes-demo ./helm
helm upgrade kubernetes-demo ./helm --set replicaCount=3
helm uninstall kubernetes-demo
```

---

### FAQ Alumno

**¿Por que mis pods entran en `CrashLoopBackOff` apenas los aplico?**
Casi siempre es que el `livenessProbe.initialDelaySeconds` es demasiado corto: Spring Boot tarda ~15-25s en arrancar y K8s lo mata antes. Aqui usamos `initialDelaySeconds: 30` en liveness precisamente por eso. Verifica con `kubectl logs <pod>` y `kubectl describe pod <pod>`.

**¿Por que existen dos probes (liveness y readiness) y no una sola?**
Son preguntas distintas. *Readiness*: "¿puedo mandarte trafico ya?" (Spring conecto a la BD, cache calentado). *Liveness*: "¿sigues vivo o estas colgado?". Si mezclas ambas, K8s podria reiniciar un pod que solo esta calentando y nunca terminaria de arrancar.

**¿Por que `resources.limits.memory: 512Mi` y no dejar libre?**
K8s aisla recursos por cgroups. Sin limite, un pod con leak se come toda la RAM del nodo y afecta a los vecinos. Con limite, K8s lo mata (`OOMKilled`) y crea uno nuevo. La flag `-XX:MaxRAMPercentage=75.0` en el Dockerfile hace que la JVM respete ese limite.

**¿Por que la imagen usa `imagePullPolicy: IfNotPresent`?**
Para desarrollo local (Docker Desktop / minikube) queremos que use la imagen local recien construida y NO intente ir a Docker Hub (donde no existe). En produccion usarias `Always` con un registry.

**¿Por que el Service es `ClusterIP` y no `LoadBalancer`?**
`ClusterIP` es interno al cluster: perfecto para que otros microservicios lo consuman. Para exponer al mundo real usamos `Ingress` (mas barato y flexible que `LoadBalancer`, que en la nube crea un balanceador de pago).

**¿Que hace el `ConfigMap` con `SPRING_PROFILES_ACTIVE=prod`?**
Spring Boot traduce variables de entorno con underscores a properties con puntos: `SPRING_PROFILES_ACTIVE` -> `spring.profiles.active`. Asi activas el perfil "prod" sin recompilar el JAR ni tocar `application.yml`.

**¿Puedo saltarme Helm?**
Si. Los manifiestos de `k8s/` funcionan sin Helm. Helm agrega valor cuando tienes que desplegar la misma app en dev/qa/prod cambiando solo unos valores (replicas, imagen, recursos). Para un solo entorno es sobreingenieria.

**¿Por que `runAsNonRoot: true` en el Deployment y `USER spring` en el Dockerfile?**
Defensa en profundidad. El `USER spring` del Dockerfile ya hace que el proceso corra como no-root. El `runAsNonRoot: true` de K8s se REHUSA a arrancar el pod si el UID resulta ser 0 (root), aunque alguien reconstruya la imagen mal. Es un cinturon + tirantes.

