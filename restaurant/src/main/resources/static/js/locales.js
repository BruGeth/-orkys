document.addEventListener("DOMContentLoaded", () => {
  const filterButtons = document.querySelectorAll(".filter-btn")
  const locationCards = document.querySelectorAll(".location-card")

  filterButtons.forEach((button) => {
    button.addEventListener("click", function () {
      // Cambiar clase activa
      filterButtons.forEach((btn) => btn.classList.remove("active"))
      this.classList.add("active")

      const filter = this.textContent.toLowerCase()

      locationCards.forEach((card) => {
        const district = card.getAttribute("data-district")

        if (filter === "todos") {
          card.style.display = "block"
        } else if (filter === "lima norte" && district === "lima-norte") {
          card.style.display = "block"
        } else if (filter === "lima sur" && district === "lima-sur") {
          card.style.display = "block"
        } else if (filter === "callao" && district === "callao") {
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

    locationCards.forEach((card) => {
      const name = card.querySelector(".location-name").textContent.toLowerCase()
      const address = card.querySelector(".location-address").textContent.toLowerCase()

      if (name.includes(searchTerm) || address.includes(searchTerm)) {
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
    const cards = document.querySelectorAll(".location-card")

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
  document.querySelectorAll(".location-card").forEach((card) => {
    card.style.opacity = "0"
    card.style.transform = "translateY(20px)"
    card.style.transition = "opacity 0.5s ease, transform 0.5s ease"
  })

  // Ejecutar animación en scroll
  window.addEventListener("scroll", animateOnScroll)
  // Ejecutar una vez al cargar para elementos visibles
  animateOnScroll()
})
