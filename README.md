# IT Orders Business Application

This complete business application consists of two services

* itorders application service
* itorders tracking service


## it orders service

This is the main entry point for employees to place their request for new IT equipment.
At the same time this service allows managers to review and approve/reject given order.
Suppliers can also interact via this service to provide recommended hardware specifications, place orders.

![Order service screenshot](itorders-service.png?raw=true)

It's implemented as SpringBoot application with business automation capability - heavy lifting is
done by case management features.

## it orders tracking service

Tracking service allows to keep an eye on placed orders and assign order numbers for tracking purposes.

![Tracking service screenshot](tracking-service.png?raw=true)

It's implemented as simple Vert.x application that keeps the state in memory only.

# How to run the application

## Launch order service

Go to itorders-application-service and launch the service
```
./launch.sh clean install     // for unix/linux
```

```
./launch.bat clean install     // for windows
```

Then, open a browser to http://localhost:8090


## Launch order tracking service

Go to itorders-tracking-service and launch the service
```
./launch.sh clean install     // for unix/linux
```

```
./launch.bat clean install     // for windows
```

Then, open a browser to http://localhost:8180

# Login to application

There are several users predefined that allow to use this application:
- maciek (maciek1!) - regular user who can place orders
- tihomir (tihomir1!) - supplier that can provide hardware specification and place orders
- krisv (krisv1!) - manager that can approve/reject orders
