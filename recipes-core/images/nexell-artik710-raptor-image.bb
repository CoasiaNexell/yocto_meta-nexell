# Base this image on nexell-hwup-image
include nexell-hwup-image.bb

SPLASH = "psplash-nexell"

IMAGE_FEATURES += "ssh-server-dropbear splash"

#DISTRO_FEATURES_remove = "x11 wayland"