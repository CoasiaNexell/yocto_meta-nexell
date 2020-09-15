DESCRIPTION = "nx_video_api_test"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=1c29f522bb1f7e5686af40aff4032b50"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/apps/nx_video_api_test"

SRC_URI = "file://${SRC_PATH}"
SRC_URI_append = " file://0001-Yocto-Dunfell-AV_CODEC_FLAG_TRUNCATED-modification.patch"

S = "${WORKDIR}/git"

PV = "NEXELL"
PR = "0.1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "ffmpeg nx-v4l2 nx-video-api nx-drm-allocator libdrm-nx libav"

inherit autotools pkgconfig

CFLAGS_append = "  -Wno-deprecated-declarations "
CXXFLAGS_append = "  -Wno-deprecated-declarations "


EXTRA_OECONF = " \
     '--prefix=${STAGING_DIR_HOST}' \
     "

# EXTRA_OEMAKE = " \
#      'video_api_test_CFLAGS = -I${STAGING_INCDIR} -I${STAGING_INCDIR}/libdrm \
#      'video_api_test_LDFLAGS = -L${STAGING_LIBDIR}' \
#      "

do_myp() {
    cd ${SRC_PATH}
    if [ -e PATCH_DONE_BY_YOCTO ];then
        ${_PATCH_FILE_REVERT_BY_GEN_}
        rm PATCH_DONE_BY_YOCTO
    fi
    ${_PATCH_FILE_BY_GEN_}
    touch PATCH_DONE_BY_YOCTO
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

_PATCH_FILE_BY_GEN_="patch -p1 < ${WORKDIR}/0001-Yocto-Dunfell-AV_CODEC_FLAG_TRUNCATED-modification.patch -f;"
_PATCH_FILE_REVERT_BY_GEN_="patch -R -p1 < ${WORKDIR}/0001-Yocto-Dunfell-AV_CODEC_FLAG_TRUNCATED-modification.patch -f;"

do_patch() {
    :
}

do_configure() {
	cd ${S}
    NOCONFIGURE=true ./autogen.sh
    oe_runconf ${EXTRA_OECONF}
}

do_compile() {
	cd ${S}
    oe_runmake clean
    oe_runmake \
		AM_CPPFLAGS+="$(WARN_CFLAGS) \
		-I. \
		-I${STAGING_INCDIR} \
		-I${STAGING_INCDIR}/libdrm \
		-I${S}/src/include \
		"
}

do_install() {
	cd ${S}
    install -d ${D}${bindir}
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP_${PN} = "installed-vs-shipped"
FILES_${PN} = "${bindir}"
ALLOW_EMPTY_${PN} = "1"