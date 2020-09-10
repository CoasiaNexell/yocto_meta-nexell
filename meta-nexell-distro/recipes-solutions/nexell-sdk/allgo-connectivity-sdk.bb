DESCRIPTION = "Nexell allgo connectivity SDK"
HOMEPAGE = "http://www.nexell.co.kr"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = " \
	libxml2 \
	libusb1 \
	nx-uds \
	nx-config \
	common-api-c++-dbus \
	common-api-c++ \
	qtbase-native \
	qtmultimedia \
	json-glib \
	protobuf-c \
	icu \
	openssl \
	gstreamer1.0-plugins-base \
"

RDEPENDS_${PN} = " \
	bash \
"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.6', 'nexell-sdk-qt5.6.x-env', \
           bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.8', 'nexell-sdk-qt5.8.x-env', \
           bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.8', 'nexell-sdk-qt5.9.x-env', '', d), d), d)}

PV = "1.0.0"
PR = "r0"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/solutions/allgo-connectivity-sdk"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"
SDK_RESULT = "${S}/result"

export OECORE_SDK_VERSION = "${SDK_VERSION}"
export TARGET_MACHINE = "${MACHINE}"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_install() {
	echo "Installing allgo connectivity SDK..."

	install -d ${D}${sysconfdir}
	install -d ${D}${libdir}
	install -d ${D}${bindir}
	install -d ${D}/home/root
	install -d ${D}/nexell/daudio
	install -d ${D}${baselibdir}
	install -d ${D}{systemd_system_unitdir}

	cp -apR ${SDK_RESULT}${sysconfdir}/* ${D}${sysconfdir}/
	chown -R root:root ${D}${sysconfdir}/
	cp -apR ${SDK_RESULT}${libdir}/* ${D}${libdir}/
	chown -R root:root ${D}${libdir}/
	cp -apR ${SDK_RESULT}${bindir}/* ${D}${bindir}/
	chown -R root:root ${D}${bindir}/
	cp -apR ${SDK_RESULT}/home/root/* ${D}/home/root/
	chmod -R 755 ${D}/home/root/*
	chown -R root:root ${D}/home/root/*
	cp -apR ${SDK_RESULT}/nexell/daudio/* ${D}/nexell/daudio/
	chmod -R 755 ${D}/nexell/daudio/*
	chown -R root:root ${D}/nexell/daudio/*
	cp -apR ${SDK_RESULT}${baselibdir}/* ${D}${baselibdir}/
	chown -R root:root ${D}${baselibdir}/
}

FILES_${PN} = "${sysconfdir} ${bindir} ${libdir} home/root nexell/daudio ${baselibdir} ${systemd_system_unitdir}"
FILES_${PN}-dev = "${includedir}"

INSANE_SKIP_${PN} = "dev-deps dev-so textrel already-stripped installed-vs-shipped"
INSANE_SKIP_${PN}-dev = "dev-elf textrel"
