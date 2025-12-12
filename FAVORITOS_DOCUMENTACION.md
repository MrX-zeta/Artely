# Funcionalidad de Favoritos - Documentación

## Resumen
Se ha implementado un sistema completo de favoritos siguiendo el patrón arquitectónico MVVM para permitir que los usuarios con rol "Customer" puedan agregar obras de arte a su lista de favoritos.

## Archivos Creados

### 1. Modelo - `Favorite.kt`
**Ubicación:** `app/src/main/java/com/luis/artelyapp/model/Favorite.kt`

- Data class que representa una obra favorita
- Propiedades:
  - `id_Favorite`: ID único del favorito
  - `id_Customer`: ID del customer que marcó el favorito
  - `id_ArtWork`: ID de la obra marcada como favorita
  - `id_Artist`: ID del artista de la obra
  - `timestamp`: Momento en que se agregó a favoritos

### 2. Repositorio - `FavoriteRepository.kt`
**Ubicación:** `app/src/main/java/com/luis/artelyapp/repository/FavoriteRepository.kt`

- Maneja todas las operaciones con Firebase Realtime Database
- Métodos principales:
  - `addFavorite()`: Agrega una obra a favoritos
  - `removeFavorite()`: Elimina una obra de favoritos
  - `isFavorite()`: Verifica si una obra está en favoritos
  - `getFavoritesByCustomer()`: Obtiene todos los favoritos con Flow
  - `getFavoritesCount()`: Cuenta total de favoritos

### 3. ViewModel - `FavoriteViewModel.kt`
**Ubicación:** `app/src/main/java/com/luis/artelyapp/viewmodel/FavoriteViewModel.kt`

- Gestiona el estado UI y la lógica de negocio
- Estados UI:
  - `Loading`: Cargando datos
  - `Success`: Datos cargados exitosamente
  - `Empty`: Sin favoritos
  - `Error`: Error al cargar
- Métodos principales:
  - `loadFavorites()`: Carga favoritos del usuario actual
  - `checkIsFavorite()`: Verifica estado de favorito
  - `addToFavorites()`: Agrega a favoritos con callback de éxito
  - `removeFromFavorites()`: Elimina de favoritos
  - `toggleFavorite()`: Alterna estado de favorito

### 4. Vista - `FavoritesScreen.kt`
**Ubicación:** `app/src/main/java/com/luis/artelyapp/view/Favorites/FavoritesScreen.kt`

- Pantalla completa para ver favoritos
- Características:
  - Grid de 2 columnas con las obras favoritas
  - Botón para eliminar cada favorito (corazón en la esquina)
  - Navegación inferior para Gallery, Chat, Favoritos y Perfil
  - Estados visuales para Loading, Empty y Error
  - Click en obra navega a detalles

## Modificaciones a Archivos Existentes

### 5. `ArtworkDetailScreen.kt`
**Modificaciones:**
- Agregado `FavoriteViewModel` como parámetro
- Agregado parámetro `onNavigateToFavorites`
- Verificación del rol del usuario (Customer/Artist)
- Nuevo botón "Agregar a favoritos" / "Quitar de favoritos"
  - Solo visible para Customers
  - No se muestra en obras propias del customer
  - Al agregar, redirige automáticamente a la pantalla de favoritos
  - Cambia de color y texto según el estado

### 6. `NavManager.kt`
**Modificaciones:**
- Agregada ruta `Screen.Favorites`
- Import de `FavoritesScreen`
- Nuevo composable para la ruta de favoritos
- Agregado parámetro `onNavigateToFavorites` en ArtworkDetailScreen
- BackHandler para favoritos (vuelve a Gallery)

## Reglas de Negocio Implementadas

### Restricciones por Rol
✅ **Solo Customers pueden:**
- Ver el botón de favoritos
- Agregar obras a favoritos
- Acceder a la pantalla de favoritos

❌ **Artists NO pueden:**
- Agregar obras a favoritos (el botón no aparece)

### Restricciones Adicionales
- Los customers NO pueden marcar sus propias obras como favoritas (si tuvieran alguna)
- Al agregar a favoritos, se redirige inmediatamente a la pantalla de favoritos
- Los favoritos se ordenan por timestamp descendente (más recientes primero)

## Flujo de Navegación

```
ArtworkDetailScreen (Customer) 
    ↓ (Click "Agregar a favoritos")
    ↓ (Obra agregada a Firebase)
    → FavoritesScreen (Redirección automática)
    
FavoritesScreen
    ↓ (Click en obra)
    → ArtworkDetailScreen (Ver detalles)
    
FavoritesScreen
    ↓ (Click botón eliminar ❤️)
    → Obra eliminada de favoritos
    → Lista actualizada automáticamente (Flow)
```

## Estructura de Firebase

```
favorites/
  ├─ {favorite_id_1}/
  │   ├─ id_Favorite: "..."
  │   ├─ id_Customer: "..."
  │   ├─ id_ArtWork: "..."
  │   ├─ id_Artist: "..."
  │   └─ timestamp: 1234567890
  └─ {favorite_id_2}/
      └─ ...
```

## Características Técnicas

### Reactive Programming
- Uso de `Flow` para actualizaciones en tiempo real
- `StateFlow` para gestión de estados UI
- Reactividad automática al agregar/eliminar favoritos

### Arquitectura MVVM
- **Model:** `Favorite.kt` - Datos puros
- **View:** `FavoritesScreen.kt` - UI Composable
- **ViewModel:** `FavoriteViewModel.kt` - Lógica y estado
- **Repository:** `FavoriteRepository.kt` - Acceso a datos

### Buenas Prácticas
- Separación de responsabilidades
- Manejo de errores con `Result<T>`
- Logging para debugging
- Callbacks para acciones asíncronas
- Estados UI bien definidos

## Cómo Usar

### Para Agregar a Favoritos:
1. El usuario (Customer) navega a una obra
2. Click en "Agregar a favoritos"
3. La obra se guarda en Firebase
4. Redirección automática a la pantalla de favoritos

### Para Eliminar de Favoritos:
1. El usuario está en la pantalla de favoritos
2. Click en el corazón (❤️) de la obra deseada
3. La obra se elimina de Firebase
4. La lista se actualiza automáticamente

### Para Ver Favoritos:
1. Desde cualquier pantalla con navegación
2. Click en el ícono/botón de Favoritos (❤️)
3. Se muestra la lista completa de favoritos

## Testing Sugerido

1. **Como Customer:**
   - Agregar obra a favoritos → debe redirigir
   - Ver lista de favoritos → debe mostrar obras
   - Eliminar favorito → debe actualizarse
   - Navegar a detalle desde favoritos → debe funcionar

2. **Como Artist:**
   - Ver detalle de obra → NO debe ver botón de favoritos
   - Intentar acceder a /favorites → verificar comportamiento

3. **Casos Edge:**
   - Sin favoritos → debe mostrar mensaje "No tienes favoritos"
   - Error de red → debe mostrar mensaje de error
   - Agregar favorito duplicado → debe manejarse correctamente

## Próximas Mejoras Sugeridas

- [ ] Animaciones al agregar/eliminar favoritos
- [ ] Indicador de carga al procesar favoritos
- [ ] Filtros y ordenamiento en la pantalla de favoritos
- [ ] Opción de compartir favoritos
- [ ] Sincronización offline
- [ ] Badge con contador de favoritos
- [ ] Búsqueda dentro de favoritos

---
**Implementado siguiendo MVVM y mejores prácticas de Android/Kotlin**

