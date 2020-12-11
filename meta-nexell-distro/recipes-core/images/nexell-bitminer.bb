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
    curl \
    vim \
    strace \
    tzdata \
"

NET_INSTALL = " \
    connman \
    connman-client \
"

CGMINER_INSTALL = " \
    cgminer \
"

# The python3 needed for SPI interface verification.
CGMINER_DEBUG = " \
    python3 \
"

SPITEST_TOOLS = " \
    spidevtest \
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
    ${CGMINER_DEBUG} \
    ${SPITEST_TOOLS} \
    ${CGMINER_INSTALL} \
    ${NET_INSTALL} \
"

