// MagnetoLib.h
#pragma once

// Define EXPORTED for any platform
#if defined _WIN32 || defined __CYGWIN__
#ifdef WIN_EXPORT
// Exporting...
#ifdef __GNUC__
#define EXPORTED __attribute__ ((dllexport))
#else
#define EXPORTED __declspec(dllexport) // Note: actually gcc seems to also supports this syntax.
#endif
#else
#ifdef __GNUC__
#define EXPORTED __attribute__ ((dllimport))
#else
#define EXPORTED __declspec(dllimport) // Note: actually gcc seems to also supports this syntax.
#endif
#endif
#define NOT_EXPORTED
#else
#if __GNUC__ >= 4
#define EXPORTED __attribute__ ((visibility ("default")))
#define NOT_EXPORTED  __attribute__ ((visibility ("hidden")))
#else
#define EXPORTED
#define NOT_EXPORTED
#endif
#endif

extern "C" EXPORTED void calculate(double data[], int nlines, double nxsrej, double hm, double B[3], double A_1[3 * 3]);

extern "C" EXPORTED double calculateHnorm(double data[], int nlines);