document.addEventListener("DOMContentLoaded", () => {
  const filterButtons = document.querySelectorAll(".filter-btn")
  const cardItems = document.querySelectorAll(".card-item")

  filterButtons.forEach((button) => {
    button.addEventListener("click", function () {
      // Cambiar clase activa
      filterButtons.forEach((btn) => btn.classList.remove("active"))
      this.classList.add("active")

      const filter = this.textContent.toLowerCase()

      cardItems.forEach((card) => {
        const category = card.getAttribute("data-rarity")

        if (filter === "todos") {
          card.style.display = "block"
        } else if (filter === "pollos" && category === "pollo") {
          card.style.display = "block"
        } else if (filter === "parrillas" && category === "parrilla") {
          card.style.display = "block"
        } else if (filter === "combos" && category === "combo") {
          card.style.display = "block"
        } else {
          card.style.display = "none"
        }
      })
    })
  })

  // Funcionalidad de búsqueda
  const searchInput = document.querySelector(".search-bar input")
  const searchButton = document.querySelector(".search-bar button")

  function performSearch() {
    const searchTerm = searchInput.value.toLowerCase()

    cardItems.forEach((card) => {
      const name = card.querySelector(".card-name").textContent.toLowerCase()
      const type = card.querySelector(".card-type").textContent.toLowerCase()
      const category = card.getAttribute("data-rarity").toLowerCase()

      if (name.includes(searchTerm) || type.includes(searchTerm) || category.includes(searchTerm)) {
        card.style.display = "block"
      } else {
        card.style.display = "none"
      }
    })
  }

  searchButton.addEventListener("click", performSearch)
  searchInput.addEventListener("keyup", (event) => {
    if (event.key === "Enter") {
      performSearch()
    }
  })

  // Animación para las tarjetas al hacer scroll
  const animateOnScroll = () => {
    const cards = document.querySelectorAll(".card-item")

    cards.forEach((card) => {
      const cardPosition = card.getBoundingClientRect().top
      const screenPosition = window.innerHeight / 1.3

      if (cardPosition < screenPosition) {
        card.style.opacity = "1"
        card.style.transform = "translateY(0)"
      }
    })
  }

  // Inicializar tarjetas con opacidad 0
  document.querySelectorAll(".card-item").forEach((card) => {
    card.style.opacity = "0"
    card.style.transform = "translateY(20px)"
    card.style.transition = "opacity 0.5s ease, transform 0.5s ease"
  })

  // Ejecutar animación en scroll
  window.addEventListener("scroll", animateOnScroll)
  // Ejecutar una vez al cargar para elementos visibles
  animateOnScroll()
})
