inherit post-process
include recipes-core/images/core-image-minimal.bb

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

IMAGE_FEATURES += " splash"

SYSTEMD_INSTALL = " \
    systemd-compat-units \
    rsyslog-systemd \
"

DEVEL_TOOLS = " \
    android-tools-nexell \
    procps \
    net-tools \
    iperf3 \
    ethtool \
    udev-extraconf \
"

CGMINER_INSTALL = " \
    cgminer-gekko \
"

WEB_INSTALL = " \
    screen \
    libmodbus \
    lighttpd \
"
#WEB_INSTALL += " \
#    modbus-tcp-server \
#"

IMAGE_INSTALL_append = " \
    kernel-modules \
    ${SYSTEMD_INSTALL} \
    ${DEVEL_TOOLS} \
    ${WEB_INSTALL} \
"

