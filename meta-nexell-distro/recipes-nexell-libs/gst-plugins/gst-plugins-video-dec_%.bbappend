### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/library/gst-plugins-video-dec"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"