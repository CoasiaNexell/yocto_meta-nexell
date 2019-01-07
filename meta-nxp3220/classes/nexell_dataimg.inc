inherit classes/nexell-mkimage.bbclass

do_data_image() {
	make_ext4_dataimg ${DEPLOY_DIR_IMAGE}
}
addtask data_image after do_image_complete before do_build

# always run task
do_data_image[nostamp] = "1"