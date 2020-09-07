#FILESEXTRAPATHS_prepend := "${THISDIR}/qtwayland_patch:"

#SRC_URI_append = " \
#    file://always_true_isExposed.patch \
#"

NX_WAYLAND_EGL_1 = "${@bb.utils.contains('DISTRO_FEATURES', 'nexell-mali-fb', 'nexell-drm-mali-sdl-fb', '', d)}"
NX_WAYLAND_EGL_2 = "${@bb.utils.contains('DISTRO_FEATURES', 'nexell-mali-wayland', 'nexell-drm-mali-sdl-wayland', '', d)}"
NX_WAYLAND_QT = "${@bb.utils.contains('DISTRO_FEATURES', 'nexell-mali-qt', 'nexell-drm-mali-qt', '', d)}"

PRIVATE_LIBS += "libMali.so"

RDEPENDS_qtwayland += " ${NX_WAYLAND_EGL_1} \
                      ${NX_WAYLAND_EGL_2} \
                      ${NX_WAYLAND_QT} \
                    "

RDEPENDS_qtwayland-plugins += " ${NX_WAYLAND_EGL_1} \
                      ${NX_WAYLAND_EGL_2} \
                      ${NX_WAYLAND_QT} \
                    "
