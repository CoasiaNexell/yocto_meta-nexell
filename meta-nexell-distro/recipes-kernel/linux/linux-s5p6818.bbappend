### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"
SRC_URI_append = " file://0001-Yocto-unaccess-changed.patch"

do_patch() {
    :
}

do_mypatch() {
    cd ${S}
    if [ -e PATCH_DONE_BY_YOCTO ];then
        ${_PATCH_FILE_REVERT_BY_GEN_}
        rm PATCH_DONE_BY_YOCTO
    fi
    ${_PATCH_FILE_BY_GEN_}
    touch PATCH_DONE_BY_YOCTO
}
addtask mypatch before do_compile after do_configure

_PATCH_FILE_BY_GEN_="patch -p1 < ${WORKDIR}/0001-Yocto-unaccess-changed.patch -f;"
_PATCH_FILE_REVERT_BY_GEN_="patch -R -p1 < ${WORKDIR}/0001-Yocto-unaccess-changed.patch -f;"


#clean .config
do_configure_prepend() {
    echo "" > ${S}/.config
}
