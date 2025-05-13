document.addEventListener("DOMContentLoaded", () => {
  // Mostrar la fecha actual
  const fecha = new Date()
  const fechaFormateada = fecha.toLocaleDateString("es-PE", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  })
  document.getElementById("fecha-registro").textContent = fechaFormateada

  // Mostrar vista previa de imagen
  const inputImagen = document.getElementById("imagen-reclamo")
  const preview = document.getElementById("preview")

  inputImagen.addEventListener("change", function () {
    const archivo = this.files[0]
    if (archivo) {
      const lector = new FileReader()
      lector.onload = (e) => {
        preview.src = e.target.result
        preview.style.display = "block"
      }
      lector.readAsDataURL(archivo)
    } else {
      preview.src = ""
      preview.style.display = "none"
    }
  })

  // Mensaje de envío del formulario
  document.getElementById("reclamacion-form").addEventListener("submit", (e) => {
    e.preventDefault()
    alert("¡Reclamación enviada con éxito!")
    e.target.reset()
    preview.style.display = "none"
  })
})
