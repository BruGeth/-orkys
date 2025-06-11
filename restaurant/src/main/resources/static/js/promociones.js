document.addEventListener("DOMContentLoaded", () => {
  // Funcionalidad de búsqueda
  const searchInput = document.querySelector(".input-search")
  const searchButton = document.querySelector(".btn-search")
  const gridItems = document.querySelectorAll(".grid-item")

  function performSearch() {
    const searchTerm = searchInput.value.toLowerCase()

    gridItems.forEach((item) => {
      const title = item.querySelector(".title-prom").textContent.toLowerCase()
      const description = item.querySelector(".info-prom").textContent.toLowerCase()

      if (title.includes(searchTerm) || description.includes(searchTerm)) {
        item.style.display = "flex"
      } else {
        item.style.display = "none"
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
    const cards = document.querySelectorAll(".grid-item")

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
  document.querySelectorAll(".grid-item").forEach((card) => {
    card.style.opacity = "0"
    card.style.transform = "translateY(20px)"
    card.style.transition = "opacity 0.5s ease, transform 0.5s ease"
  })

  // Ejecutar animación en scroll
  window.addEventListener("scroll", animateOnScroll)
  // Ejecutar una vez al cargar para elementos visibles
  animateOnScroll()
})
