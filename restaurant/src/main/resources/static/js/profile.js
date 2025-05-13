document.addEventListener("DOMContentLoaded", () => {
  // Botones para editar perfil
  const editBtn = document.getElementById("edit-profile-btn")
  const saveBtn = document.getElementById("save-profile-btn")
  const cancelBtn = document.getElementById("cancel-edit-btn")

  // Campos del formulario
  const inputs = document.querySelectorAll("#profile-info input")

  // Valores originales para restaurar en caso de cancelar
  const originalValues = {}

  // Habilitar edición
  if (editBtn) {
    editBtn.addEventListener("click", () => {
      // Guardar valores originales
      inputs.forEach((input) => {
        originalValues[input.id] = input.value
        input.removeAttribute("readonly")
      })

      // Mostrar/ocultar botones
      editBtn.classList.add("d-none")
      saveBtn.classList.remove("d-none")
      cancelBtn.classList.remove("d-none")
    })
  }

  // Cancelar edición
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      // Restaurar valores originales
      inputs.forEach((input) => {
        input.value = originalValues[input.id]
        input.setAttribute("readonly", true)
      })

      // Mostrar/ocultar botones
      editBtn.classList.remove("d-none")
      saveBtn.classList.add("d-none")
      cancelBtn.classList.add("d-none")
    })
  }

  // Activar pestaña según hash en URL
  const hash = window.location.hash
  if (hash) {
    const tab = document.querySelector(`a[href="${hash}"]`)
    if (tab) {
      tab.click()
    }
  }

  // Actualizar URL al cambiar de pestaña
  const tabLinks = document.querySelectorAll('.nav-link[data-bs-toggle="tab"]')
  tabLinks.forEach((link) => {
    link.addEventListener("shown.bs.tab", (e) => {
      history.pushState(null, null, e.target.getAttribute("href"))
    })
  })
})
