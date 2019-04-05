#include <jni.h>
#include <string>
#include "darknet/src/darknet.h"

#define true JNI_TRUE
#define false JNI_FALSE

//rewrite test demo for android
double rewrite_test_detector(char *datacfg, char *cfgfile, char *weightfile, char *filename, float thresh, float hier_thresh, char *outfile, int fullscreen,char* sdpath)
{
    LOGD("data=%s",datacfg);
    LOGD("cfg=%s",cfgfile);
    LOGD("wei=%s",weightfile);
    LOGD("img=%s",filename);

    //list *options = read_data_cfg(datacfg);
    char *name_list=new char[100];
    strcpy(name_list,sdpath);
    strcat(name_list,"yolo/data/coco.names");//option_find_str(options, "names", "data/names.list");
    char **names = get_labels(name_list);

//    char **names=(char**)malloc(180*sizeof(char *));
//    names[0]="person";
//    names[1]="dog";



    image **alphabet = new_load_alphabet(sdpath);
    network *net = load_network(cfgfile, weightfile, 0);
    set_batch_network(net, 1);
    srand(2222222);
    double time;
    char buff[256];
    char *input = buff;
    int j;
    float nms=.3;
    while(1){
        if(filename){
            strncpy(input, filename, 256);
        } else {
            printf("Enter Image Path: ");
            fflush(stdout);
            input = fgets(input, 256, stdin);
            if(!input) return 0;
            strtok(input, "\n");
        }
        image im = load_image_color(input,0,0);
        image sized = letterbox_image(im, net->w, net->h);
        //image sized = resize_image(im, net->w, net->h);
        //image sized2 = resize_max(im, net->w);
        //image sized = crop_image(sized2, -((net->w - sized2.w)/2), -((net->h - sized2.h)/2), net->w, net->h);
        //resize_network(net, sized.w, sized.h);
        layer l = net->layers[net->n-1];

        box *boxes = (box*)calloc(l.w*l.h*l.n, sizeof(box));
        float **probs = (float**)calloc(l.w*l.h*l.n, sizeof(float *));
        for(j = 0; j < l.w*l.h*l.n; ++j) probs[j] = (float*)calloc(l.classes + 1, sizeof(float *));
        float **masks = 0;
        if (l.coords > 4){
            masks = (float**)calloc(l.w*l.h*l.n, sizeof(float*));
            for(j = 0; j < l.w*l.h*l.n; ++j) masks[j] = (float*)calloc(l.coords-4, sizeof(float *));
        }

        float *X = sized.data;
        time=what_time_is_it_now();
        network_predict(net, X);
        time = what_time_is_it_now()-time;
        LOGD("%s: Predicted in %f seconds.\n", input, time);
        int nboxes = 0;
        detection *dets = get_network_boxes(net, im.w, im.h, thresh, hier_thresh, 0, 1, &nboxes);
        //printf("%d\n", nboxes);
        //if (nms) do_nms_obj(boxes, probs, l.w*l.h*l.n, l.classes, nms);
        if (nms) do_nms_sort(dets, nboxes, l.classes, nms);
        draw_detections(im, dets, nboxes, thresh, names, alphabet, l.classes);
        free_detections(dets, nboxes);
        if(outfile){
            save_image(im, outfile);
        }
        else{
            save_image(im, "predictions");
#ifdef OPENCV
            cvNamedWindow("predictions", CV_WINDOW_NORMAL);
            if(fullscreen){
                cvSetWindowProperty("predictions", CV_WND_PROP_FULLSCREEN, CV_WINDOW_FULLSCREEN);
            }
            show_image(im, "predictions");
            cvWaitKey(0);
            cvDestroyAllWindows();
#endif
        }

        free_image(im);
        free_image(sized);
//        free(boxes);
//        free_ptrs((void **)probs, l.w*l.h*l.n);
        free_network(net);
        if (filename)
            break;
    }
    return time;
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_demo7_Yolo_inityolo(JNIEnv *env, jobject instance, jstring cfgfile_,
                                     jstring weightfile_) {
    const char *cfgfile = env->GetStringUTFChars(cfgfile_, 0);
    const char *weightfile = env->GetStringUTFChars(weightfile_, 0);

    // TODO

    env->ReleaseStringUTFChars(cfgfile_, cfgfile);
    env->ReleaseStringUTFChars(weightfile_, weightfile);
    return;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_com_example_demo7_Yolo_testyolo(JNIEnv *env, jobject instance, jstring imgfile_,jstring sdpath_) {
    const char *imgfile = env->GetStringUTFChars(imgfile_, 0);

    // TODO
    double time;
    const char *temp_imgfile_str = env->GetStringUTFChars(imgfile_, 0);
    char* imgfile_str=new char[100];
    strcpy(imgfile_str,temp_imgfile_str);

    const char *temp_sdpath_str = env->GetStringUTFChars(sdpath_, 0);
    char* sdpath_str=new char[100];
    strcpy(sdpath_str,temp_sdpath_str);

    char *datacfg_str =new char[100];
    strcpy(datacfg_str,sdpath_str);
    strcat(datacfg_str,"yolo/cfg/coco.data");

    char *cfgfile_str=new char[100];
    strcpy(cfgfile_str,sdpath_str);
    strcat(cfgfile_str,"yolo/cfg/yolov3-tiny.cfg");

    char *weightfile_str=new char[100];
    strcpy(weightfile_str,sdpath_str);
    strcat(weightfile_str,"yolo/weights/yolov3-tiny.weights");

    //char *imgfile_str = "/storage/0123-4567/yolo/data/dog.jpg";
    char *outimgfile_str=new char[100];
    strcpy(outimgfile_str,sdpath_str);
    strcat(outimgfile_str,"yolo/out");

    time = rewrite_test_detector(datacfg_str, cfgfile_str,
                         weightfile_str, imgfile_str,
                         0.2f, 0.5f, outimgfile_str, 0, sdpath_str);
    env->ReleaseStringUTFChars(imgfile_, imgfile);
    return time;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_demo7_Yolo_detectimg(JNIEnv *env, jobject instance, jobject dst, jobject src) {

    // TODO

}