### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"

#clean .config
do_configure_prepend() {
    echo "" > ${S}/.config
}





#Override for local source using evironment

LINUX_VERSION = "4.4.83"
PV = "4.4.83"

_SRC_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/kernel/./kernel-4.4.x"
_MOV_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/./kernel-4.4.x"

#Override for local source using evironment

LINUX_VERSION = "4.4.83"
PV = "4.4.83"

_SRC_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/kernel/./kernel-4.4.x"
_MOV_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/./kernel-4.4.x"

#Override for local source using evironment

LINUX_VERSION = "4.4.83"
PV = "4.4.83"

_SRC_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/kernel/./kernel-4.4.x"
_MOV_PATH_BY_GEN_="/home/mcjino/hdd2/yocto/internal/yocto_sumo_dev/vendor/nexell/./kernel-4.4.x"