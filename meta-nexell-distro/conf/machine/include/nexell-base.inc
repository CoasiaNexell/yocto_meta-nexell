inherit linux-nexell-base

SOC_FAMILY = "Nexell"

NEXELL_BOARD_SOCNAME ?= "${BSP_TARGET_SOCNAME}"

IMAGE_FSTYPES ?= "tar.bz2 ext4"
INITRAMFS_IMAGE ?= "nexell-ramdisk"

# If you need to change below function, you can see the classes/linux-nexell-base.bbclass
KERNEL_IMAGETYPE_${MACHINE} ?= "${@get_kernel_image_type(d,"${MACHINE}")}"
DEFAULTTUNE_${MACHINE} ?= "${@get_defaulttune_type(d,"${MACHINE}")}"

MACHINE_FEATURES += "apm usbhost keyboard vfat ext2 screen mouse touchscreen bluetooth wifi sdio"
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"
MACHINE_EXTRA_RRECOMMENDS += "kernel-modules kernel-devicetree"

DEFAULT_TIMEZONE="Asia/Seoul"

IMAGE_OVERHEAD_FACTOR="1.0"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-${NEXELL_BOARD_SOCNAME}"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-nexell"

# default type is pressure sensitive touch screen
# If yoc need another type, override 'NEXELL_TOUCH_CLASS' in machine/conf/<board_name>
NEXELL_TOUCH_CLASS = "PRESSES"

# nexell device id
NEXELL_DEVID_USB ?= "0"
NEXELL_DEVID_SPI ?= "1"
NEXELL_DEVID_NAND ?= "2"
NEXELL_DEVID_SDMMC ?= "3"
NEXELL_DEVID_SDFS ?= "4"
NEXELL_DEVID_UART ?= "5"

# nexell device port number
NEXELL_PORT_EMMC ?= "2"
NEXELL_PORT_SD ?= "0"

BSP_OUTPUT_DIR_NAME ?= "result-${BSP_TARGET_MACHINE}-${BSP_TARGET_IMAGE_TYPE}"
BSP_OUTPUT_DIR_PATH ?= "${BSP_ROOT_DIR}/out/${BSP_OUTPUT_DIR_NAME}"

# for ubuntu
NEXELL_RELEASE_SERVER_ADDRESS ?= "http://192.168.1.25:8070"
UBUNTU_CONSOLE_VERSION_TRUSTY ?= "ubuntu-rootfs-console-trusty.tar.gz"
UBUNTU_CONSOLE_VERSION_XENIAL ?= "ubuntu-rootfs-console-xenial.tar.gz"
UBUNTU_DESKTOP_VERSION_TRUSTY ?= "ubuntu-rootfs-desktop.tar.gz"
UBUNTU_DESKTOP_LXDE_VERSION ?= "ubuntu-rootfs-desktop-lxde.tar.gz"
UBUNTU_ROOTFS="${UBUNTU_CONSOLE_VERSION_TRUSTY}"

UBUNTU_KSELFTESTS ?= "kselftests.tar.gz"
UBUNTU_NX_TESTSUITE ?= "testsuite.tar.gz"
UBUNTU_NX_LIBS ?= "nxlibs.tar.gz"

UBUNTU_IMAGE_LOCATION="/${BSP_TARGET_SOCNAME}/ubuntu/rootfs/"