# IT Orders service

This is the main entry point for employees to place their request for new IT equipment.
At the same time this service allows managers to review and approve/reject given order.
Suppliers can also interact via this service to provide recommended hardware specifications, place orders.

## Launch service

```
./launch.sh clean install     // for unix/linux
```

```
./launch.bat clean install     // for windows
```

Then, open a browser to http://localhost:8090

# Login to application

There are several users predefined that allow to use this application:
- maciek (maciek1!) - regular user who can place orders
- tihomir (tihomir1!) - supplier that can provide hardware specification and place orders
- krisv (krisv1!) - manager that can approve/reject orders
