package com.luis.artelyapp.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GalleryViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val sample = listOf(
        Artwork(1, "Noche Estrellada", "Vincent van Gogh", "Una de las obras más reconocidas de Van Gogh, pintada en 1889."),
        Artwork(2, "La Gioconda", "Leonardo da Vinci"),
        Artwork(3, "David", "Miguel Ángel"),
        Artwork(4, "Impresión, sol naciente", "Claude Monet"),
        Artwork(5, "Composición VIII", "Wassily Kandinsky"),
        Artwork(6, "Retrato Desconocido", "Artista Anónimo")
    )

    private val _artworks = MutableStateFlow(sample)
    val artworks: StateFlow<List<Artwork>> = _artworks

    fun setTab(index: Int) {
        _selectedTab.value = index
    }
}

