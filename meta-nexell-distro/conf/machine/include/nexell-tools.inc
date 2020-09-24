# for tools path
NEXELL_TOOLS_PATH = "${BSP_ROOT_DIR}/layers/meta-nexell/meta-nexell-distro/tools"
NEXELL_SECURE_TOOLS_PATH = "${NEXELL_TOOLS_PATH}/secure_tools"
NEXELL_CONVERT_TOOLS_PATH = "${NEXELL_TOOLS_PATH}/convert_tools"
NEXELL_NSIH_TOOLS_PATH = "${NEXELL_CONVERT_TOOLS_PATH}"
NEXELL_BOOTLOGO_PATH = "${NEXELL_TOOLS_PATH}/bootlogo"
NEXELL_FUSING_TOOLS_PATH = "${NEXELL_TOOLS_PATH}/fusing_tools"
NEXELL_BOARD_PARTMAP_PATH = "${NEXELL_TOOLS_PATH}/configs/board_partmap"

# for Secure bin
NEXELL_SECURE_BINGEN = "${NEXELL_SECURE_TOOLS_PATH}/SECURE_BINGEN"
NEXELL_RSA_SIGN_PSS = "${NEXELL_SECURE_TOOLS_PATH}/rsa_sign_pss"
NEXELL_NSIHTXTMOD = "${NEXELL_NSIH_TOOLS_PATH}/nsihtxtmod_yocto.py"
NEXELL_NSIHBINGEN = "${NEXELL_NSIH_TOOLS_PATH}/nsihbingen_yocto.py"
NEXELL_NSIHDUMMYFILE = "${NEXELL_NSIH_TOOLS_PATH}/nsih-dummy.txt"

#NEXELL_AES_KEY =
NEXELL_PRIVATE_KEY = "${NEXELL_SECURE_TOOLS_PATH}/private_key.pem"

# For make image
NEXELL_TOOL_MKIMAGE = "${NEXELL_CONVERT_TOOLS_PATH}/mkimage"
NEXELL_TOOL_MKENVIMAGE = "${NEXELL_CONVERT_TOOLS_PATH}/mkenvimage"
NEXELL_TOOL_MAKE_EXT4FS = "${NEXELL_CONVERT_TOOLS_PATH}/make_ext4fs"
NEXELL_TOOL_MKROOTFS_IMAGE = "${NEXELL_CONVERT_TOOLS_PATH}/mkrootfs_image.sh"
NEXELL_TOOL_EXT2SIMG = "${NEXELL_CONVERT_TOOLS_PATH}/ext2simg"